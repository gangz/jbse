package jbse.algo;

import jbse.Type;
import jbse.Util;
import jbse.bc.Signature;
import jbse.exc.algo.CannotInvokeNativeException;
import jbse.exc.common.UnexpectedInternalException;
import jbse.exc.mem.InvalidOperandException;
import jbse.exc.mem.InvalidProgramCounterException;
import jbse.exc.mem.InvalidTypeException;
import jbse.exc.mem.ThreadStackEmptyException;
import jbse.exc.mem.ValueDoesNotSupportNativeException;
import jbse.mem.FunctionApplication;
import jbse.mem.Null;
import jbse.mem.Primitive;
import jbse.mem.State;
import jbse.mem.Value;

/**
 * Implements native method invocation by assuming that the invoked 
 * method is pure, i.e., it does not produce any effect other than 
 * returning a value on the operand stack according to the method's 
 * signature. More precisely:
 * <ul>
 * <li>If the method's return type is {@code void}, then the 
 *     method invocation has no effect;</li>
 * <li>If the method's return type is primitive, and all its parameters 
 *     have primitive type and are not symbolic, then {@link NativeInvokerReflect}
 *     is used to execute the native method, and the corresponding value is
 *     pushed on the operand stack;</li>
 * <li>If the method's return type is primitive, and all its parameters 
 *     have primitive type and some is symbolic, then a {@link FunctionApplication}  
 *     mirroring the method's invocation is pushed on the operand stack;</li>
 * <li>If the method's return type is reference (instance or array), 
 *     then {@link Null} is pushed on the operand stack;</li>
 * <li>In all the other cases a {@link ValueDoesNotSupportNativeException} 
 *     exception is raised.</li>
 * </ul>
 * 
 * @author Pietro Braione
 */
public class NativeInvokerPure implements NativeInvoker {
	@Override
	public void doInvokeNative(State state, Signature methodSignatureResolved, Value[] args, int pcOffset) 
	throws CannotInvokeNativeException, UnexpectedInternalException, ThreadStackEmptyException {
		//determines the return value
		final String returnType = Type.splitReturnValueDescriptor(methodSignatureResolved.getDescriptor());
		final Value returnValue;
		if (Type.isVoid(returnType)) {
			returnValue = null;
		} else if (Type.isPrimitive(returnType)) {
			//requires all arguments are primitive
			final Primitive[] argsPrim = new Primitive[args.length];
			boolean someSymbolic = false;
			for (int i = 0; i < args.length; i++) {
				if (args[i] instanceof Primitive) {
					argsPrim[i] = (Primitive) args[i];
					someSymbolic = someSymbolic || (argsPrim[i].isSymbolic());
				} else { 
					throw new ValueDoesNotSupportNativeException();
				}
			}
			if (someSymbolic) {
				try {
					returnValue = state.getCalculator().applyFunction(returnType.charAt(0), methodSignatureResolved.getName(), argsPrim);
				} catch (InvalidOperandException | InvalidTypeException e) {
					//this should never happen
					throw new UnexpectedInternalException(e);
				}
			} else {
				final NativeInvokerReflect delegate = new NativeInvokerReflect();
				delegate.doInvokeNative(state, methodSignatureResolved, argsPrim, pcOffset);
				return;
			}
		} else {
			returnValue = Null.getInstance();
			//TODO put reference resolution here or in the invoke* bytecodes and assign returnValue = state.createSymbol(returnType, "__NATIVE[" + state.getIdentifier() + "[" + state.getSequenceNumber() + "]");
		}
		
		//pushes the return value (if present) on the operand stack, 
		//or sets the state to stuck if no current frame exists
		try {
			if (returnValue != null) {
				state.push(returnValue);
			}
		} catch (ThreadStackEmptyException e) {
			state.setStuckReturn(returnValue);
			return;
		}		

		//increments the program counter
		try {
			state.incPC(pcOffset);
		} catch (InvalidProgramCounterException e) {
			state.createThrowableAndThrowIt(Util.VERIFY_ERROR);
		}
	}
}
