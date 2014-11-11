package jbse.algo;

import static jbse.algo.Util.ILLEGAL_ACCESS_ERROR;
import static jbse.algo.Util.INCOMPATIBLE_CLASS_CHANGE_ERROR;
import static jbse.algo.Util.NO_CLASS_DEFINITION_FOUND_ERROR;
import static jbse.algo.Util.NO_SUCH_FIELD_ERROR;
import static jbse.algo.Util.ensureKlass;

import jbse.Util;
import jbse.bc.ClassFile;
import jbse.bc.ClassHierarchy;
import jbse.bc.Signature;
import jbse.exc.bc.AttributeNotFoundException;
import jbse.exc.bc.ClassFileNotFoundException;
import jbse.exc.bc.FieldNotAccessibleException;
import jbse.exc.bc.FieldNotFoundException;
import jbse.exc.bc.InvalidIndexException;
import jbse.exc.common.UnexpectedInternalException;
import jbse.exc.dec.DecisionException;
import jbse.exc.mem.ContradictionException;
import jbse.exc.mem.InvalidProgramCounterException;
import jbse.exc.mem.ThreadStackEmptyException;
import jbse.jvm.ExecutionContext;
import jbse.mem.ConstantPoolString;
import jbse.mem.State;
import jbse.mem.Value;

/**
 * Command managing the getstatic bytecode. It decides over the value 
 * loaded to the operand stack in the case this is a symbolic reference 
 * ("lazy initialization").
 * 
 * @author Pietro Braione
 */
final class SEGetstatic extends MultipleStateGeneratorLFLoad implements Algorithm {
	
	@Override
    public void exec(State state, ExecutionContext ctx) 
    throws DecisionException, ContradictionException, 
    ThreadStackEmptyException, UnexpectedInternalException {
        //gets the index of the field signature in the current class 
    	//constant pool
        final int index;
        try {
        	final byte tmp1 = state.getInstruction(1);
        	final byte tmp2 = state.getInstruction(2);
	        index = Util.byteCat(tmp1, tmp2);
		} catch (InvalidProgramCounterException e) {
			state.createThrowableAndThrowIt(Util.VERIFY_ERROR);
			return;
		}

		//gets the field signature from the current class constant pool
        final String currentClassName = state.getCurrentMethodSignature().getClassName();
        final ClassHierarchy hier = state.getClassHierarchy();
        final Signature fieldSignature;
        try {
			fieldSignature = hier.getClassFile(currentClassName).getFieldSignature(index);
		} catch (InvalidIndexException e) {
			state.createThrowableAndThrowIt(Util.VERIFY_ERROR);
			return;
		} catch (ClassFileNotFoundException e) {
			//this should never happen
			throw new UnexpectedInternalException(e);
		}

		//performs field resolution
        final Signature fieldSignatureResolved;
		try {
	        fieldSignatureResolved = hier.resolveField(currentClassName, fieldSignature);
		} catch (ClassFileNotFoundException e) {
			state.createThrowableAndThrowIt(NO_CLASS_DEFINITION_FOUND_ERROR);
			return;
		} catch (FieldNotFoundException e) {
			state.createThrowableAndThrowIt(NO_SUCH_FIELD_ERROR);
			return;
		} catch (FieldNotAccessibleException e) {
			state.createThrowableAndThrowIt(ILLEGAL_ACCESS_ERROR);
			return;
		}

		//gets resolved field's data
		final String fieldClassName = fieldSignatureResolved.getClassName();        
		final ClassFile fieldClassFile;
		final boolean isFieldConstant;
		try {
			fieldClassFile = hier.getClassFile(fieldClassName);
			isFieldConstant = fieldClassFile.isFieldConstant(fieldSignatureResolved);
		} catch (ClassFileNotFoundException | FieldNotFoundException e) {
			//this should never happen after field resolution
			throw new UnexpectedInternalException(e);
		}

		//checks that the field is static or belongs to an interface
		try {
			if ((!fieldClassFile.isInterface()) && (!fieldClassFile.isFieldStatic(fieldSignatureResolved))) {
				state.createThrowableAndThrowIt(INCOMPATIBLE_CLASS_CHANGE_ERROR);
				return;
			}
		} catch (FieldNotFoundException e) {
			//this should never happen after field resolution
			throw new UnexpectedInternalException(e);
		}
		
		//gets the field's value, possibly performing class initialization
        Value tmpValue;
    	if (isFieldConstant) {
    		//when the field is constant, no class initialization is performed
    		//TODO where is specified this behavior in the JVM specification???
    		try {
				tmpValue = state.getCalculator().val_(fieldClassFile.fieldConstantValue(fieldSignatureResolved));
				if (tmpValue instanceof ConstantPoolString) {
					tmpValue = state.referenceToStringLiteral(tmpValue.toString());
				}
				//TODO check type!
			} catch (InvalidIndexException e) {
				state.createThrowableAndThrowIt(Util.VERIFY_ERROR);
				return;
			} catch (FieldNotFoundException | AttributeNotFoundException e) {
				throw new UnexpectedInternalException(e);
			}
    	} else {
    		final boolean mustExit;
			try {
				mustExit = ensureKlass(state, fieldClassName, ctx.decisionProcedure);
			} catch (ClassFileNotFoundException e) {
				throw new UnexpectedInternalException(e);
			}
    		if (mustExit) {
    			return;
            	//now the execution continues with the class 
            	//initialization code; the current bytecode will 
            	//be reexecuted after that
    		}
    		tmpValue = state.getKlass(fieldClassName).getFieldValue(fieldSignatureResolved);
    	}
        
        //generates all the next states
    	this.state = state;
    	this.ctx = ctx;
    	this.pcOffset = 3;
    	this.valToLoad = tmpValue;
    	generateStates();
    } 
}
