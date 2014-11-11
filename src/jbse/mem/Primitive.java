package jbse.mem;

import jbse.Type;
import jbse.exc.common.UnexpectedInternalException;
import jbse.exc.mem.InvalidOperandException;
import jbse.exc.mem.InvalidOperatorException;
import jbse.exc.mem.InvalidTypeException;

/**
 * Class that represent a primitive type value.
 */
public abstract class Primitive extends Value implements Cloneable {
	/** {@link Calculator} to perform operation on primitives. */
	protected final Calculator calc;
	
	/**
	 * Constructor. 
	 * 
	 * @param type a {@code char}, the type of this value.
	 * @param calc a {@link Calculator}.
	 * @throws InvalidTypeException if {@code type} is not primitive.
	 */
	Primitive(char type, Calculator calc) throws InvalidTypeException {
		super(type);
		if (Type.isPrimitive(type)) {
			this.calc = calc;
		} else {
			throw new InvalidTypeException(type + " is not a primitive type");
		}
	}
	
	/**
	 * Accepts a {@link PrimitiveVisitor}.
	 * 
	 * @param v a {@link PrimitiveVisitor}.
	 * @throws Exception whenever {@code v} throws an {@link Exception}.
	 */
    public abstract void accept(PrimitiveVisitor v) throws Exception;

	/**
	 * Checks whether this value denotes the primitive true value.
	 *  
	 * @return {@code true} iff the value denotes the concrete primitive 
	 *         true value.
	 *         Note that symbolic {@link Primitive}s do not denote 
	 *         the true or the false value.
	 */
	public abstract boolean surelyTrue();
	
	/**
	 * Checks whether this value is the primitive false value.
	 *  
	 * @return {@code true} iff the value denotes the concrete primitive 
	 *         false value.
	 *         Note that symbolic {@link Primitive}s do not denote 
	 *         the true or the false value.
	 */
	public abstract boolean surelyFalse();
    
    /**
     * Converts this {@link Primitive} to a corresponding {@link Primitive}
     * with another type by applying a widening conversion.
     * 
     * @param type the destination type of the conversion.
     * @return this {@link Primitive} after widening to {@code type}.
     * @throws InvalidTypeException if {@code this} cannot be widened to {@code type}.
     * @throws UnexpectedInternalException 
     */
    public Primitive widen(char type) 
    throws InvalidTypeException, UnexpectedInternalException {
    	try {
			return this.calc.widen(type, this);
		} catch (InvalidOperandException e) {
			//this should never happen
			throw new UnexpectedInternalException(e);
		}
    }
    
    /**
     * Converts this {@link Primitive} to a corresponding {@link Primitive}
     * with another type by applying a narrowing conversion.
     * 
     * @param type the destination type of the conversion.
     * @return this {@link Primitive} after narrowing to {@code type}.
     * @throws InvalidTypeException if {@code this} cannot be narrowed to {@code type}.
     * @throws UnexpectedInternalException 
     */
    public Primitive narrow(char type)
    throws InvalidTypeException, UnexpectedInternalException {
    	try {
    		return this.calc.narrow(type, this);
		} catch (InvalidOperandException e) {
			//this should never happen
			throw new UnexpectedInternalException(e);
		}
    }
    
    /**
     * Calculates the sum of {@code this} and another value.
     * 
     * @param param the {@link Primitive} representing the value to 
     *              be added to {@code this}.
     * @return a {@link Primitive} representing the result of the 
     *         operation.
     * @throws InvalidTypeException 
     * @throws InvalidOperandException 
     * @throws UnexpectedInternalException 
     */
    public Primitive add(Primitive param) 
    throws InvalidOperandException, InvalidTypeException, UnexpectedInternalException {
        return this.calc.add(this, param);
    }
    
    /**
     * Calculates the product of {@code this} and another value.
     * 
     * @param param the {@link Primitive} representing the value to 
     *              be multiplied to {@code this}.
     * @return a {@link Primitive} representing the result of the 
     *         operation.
     * @throws InvalidTypeException 
     * @throws InvalidOperandException 
     * @throws UnexpectedInternalException 
     */
    public Primitive mul(Primitive param)
    throws InvalidOperandException, InvalidTypeException, UnexpectedInternalException {
        return this.calc.mul(this, param);
    }
    
    /**
     * Calculates the difference between {@code this} and another value.
     * 
     * @param param the {@link Primitive} representing the value to 
     *              be subtracted from {@code this}.
     * @return a {@link Primitive} representing the result of the 
     *         operation.
     * @throws InvalidTypeException 
     * @throws InvalidOperandException 
     * @throws UnexpectedInternalException 
     */
    public Primitive sub(Primitive param)
    throws InvalidOperandException, InvalidTypeException, UnexpectedInternalException {
        return this.calc.sub(this, param);

    }
    
    /**
     * Calculates the result of the division of {@code this} by
     * another value.
     * 
     * @param param the {@link Primitive} representing the value to 
     *              which {@code this} must be divided.
     * @return a {@link Primitive} representing the result of the 
     *         operation.
     * @throws InvalidTypeException 
     * @throws InvalidOperandException 
     * @throws UnexpectedInternalException 
     */
    public Primitive div(Primitive param)
    throws InvalidOperandException, InvalidTypeException, UnexpectedInternalException {
        return this.calc.div(this, param);
    }
    
    /**
     * Calculates the result of the remainder of {@code this} by
     * another value.
     * 
     * @param param the {@link Primitive} representing the value to 
     *              which {@code this} must be divided.
     * @return a {@link Primitive} representing the remainder of the 
     *         division.
     * @throws InvalidTypeException 
     * @throws InvalidOperandException 
     * @throws UnexpectedInternalException 
     */
    public Primitive rem(Primitive param)
    throws InvalidOperandException, InvalidTypeException, UnexpectedInternalException {
        return this.calc.rem(this, param);
    }
    
    /**
     * Calculates the result of the arithmetic negation of {@code this}.
     * 
     * @return a {@link Primitive} representing the arithmetic negation
     *         of {@code this}.
     * @throws InvalidTypeException 
     * @throws InvalidOperandException 
     * @throws UnexpectedInternalException 
     */
    public Primitive neg()
    throws InvalidTypeException, UnexpectedInternalException {
        try {
			return this.calc.neg(this);
		} catch (InvalidOperandException e) {
			//this should never happen
			throw new UnexpectedInternalException(e);
		}
    }
    
    /**
     * Calculates the result of the left shift of {@code this}
     * by a given amount.
     * 
     * @param param the {@link Primitive} representing the amount
     *        of the shift.
     * @return a {@link Primitive} representing the result of the 
     *         operation.
     * @throws InvalidTypeException 
     * @throws InvalidOperandException 
     * @throws UnexpectedInternalException 
     */
    public Primitive shl(Primitive param)
    throws InvalidOperandException, InvalidTypeException, UnexpectedInternalException {
        return this.calc.shl(this, param);
    }
    
    /**
     * Calculates the result of the arithmetic right shift of {@code this}
     * by a given amount.
     * 
     * @param param the {@link Primitive} representing the amount
     *        of the shift.
     * @return a {@link Primitive} representing the result of the 
     *         operation.
     * @throws InvalidTypeException 
     * @throws InvalidOperandException 
     * @throws UnexpectedInternalException 
     */
    public Primitive shr(Primitive param)
    throws InvalidOperandException, InvalidTypeException, UnexpectedInternalException {
        return this.calc.shr(this, param);
    }
    
    /**
     * Calculates the result of the logical right shift of {@code this}
     * by a given amount.
     * 
     * @param param the {@link Primitive} representing the amount
     *        of the shift.
     * @return a {@link Primitive} representing the result of the 
     *         operation.
     * @throws InvalidTypeException 
     * @throws InvalidOperandException 
     * @throws UnexpectedInternalException 
     */
    public Primitive ushr(Primitive param)
    throws InvalidOperandException, InvalidTypeException, UnexpectedInternalException {
        return this.calc.ushr(this, param);
    }
    
    /**
     * Calculates the result of the bitwise AND of {@code this}
     * with another value.
     * 
     * @param param the {@link Primitive} representing the value to 
     *              which {@code this} must be put in bitwise AND.
     * @return a {@link Primitive} representing the result of the 
     *         operation.
     * @throws InvalidTypeException 
     * @throws InvalidOperandException 
     * @throws UnexpectedInternalException 
     */
    public Primitive andBitwise(Primitive param)
    throws InvalidOperandException, InvalidTypeException, UnexpectedInternalException {
        return this.calc.andBitwise(this, param);
    }
    
    /**
     * Calculates the result of the bitwise OR of {@code this}
     * with another value.
     * 
     * @param param the {@link Primitive} representing the value to 
     *              which {@code this} must be put in bitwise OR.
     * @return a {@link Primitive} representing the result of the 
     *         operation.
     * @throws InvalidTypeException 
     * @throws InvalidOperandException 
     * @throws UnexpectedInternalException 
     */
    public Primitive orBitwise(Primitive param)
    throws InvalidOperandException, InvalidTypeException, UnexpectedInternalException {
        return this.calc.orBitwise(this, param);
    }
    
    
    /**
     * Calculates the result of the bitwise XOR of {@code this}
     * with another value.
     * 
     * @param param the {@link Primitive} representing the value to 
     *              which {@code this} must be put in bitwise XOR.
     * @return a {@link Primitive} representing the result of the 
     *         operation.
     * @throws InvalidTypeException 
     * @throws InvalidOperandException 
     * @throws UnexpectedInternalException 
     */
    public Primitive xorBitwise(Primitive param)
    throws InvalidOperandException, InvalidTypeException, UnexpectedInternalException {
        return this.calc.xorBitwise(this, param);
    }
    
    /**
     * Calculates the result of the logical AND of {@code this}
     * with another value.
     * 
     * @param param the {@link Primitive} representing the value to 
     *              which {@code this} must be put in logical AND.
     * @return a {@link Primitive} representing the result of the 
     *         operation.
     * @throws InvalidTypeException 
     * @throws InvalidOperandException 
     * @throws UnexpectedInternalException 
     */
    public Primitive and(Primitive param)
    throws InvalidOperandException, InvalidTypeException, UnexpectedInternalException {
        return this.calc.and(this, param);
    }
    
    /**
     * Calculates the result of the logical OR of {@code this}
     * with another value.
     * 
     * @param param the {@link Primitive} representing the value to 
     *              which {@code this} must be put in logical OR.
     * @return a {@link Primitive} representing the result of the 
     *         operation.
     * @throws InvalidTypeException 
     * @throws InvalidOperandException 
     * @throws UnexpectedInternalException 
     */
    public Primitive or(Primitive param)
    throws InvalidOperandException, InvalidTypeException, UnexpectedInternalException {
        return this.calc.or(this, param);
    }
    
    /**
     * Calculates the result of the logical NOT of {@code this}.
     * 
     * @return a {@link Primitive} representing the result of the 
     *         operation.
     * @throws InvalidTypeException 
     * @throws InvalidOperandException 
     * @throws UnexpectedInternalException 
     */
    public Primitive not()
    throws InvalidTypeException, UnexpectedInternalException {
        try {
			return this.calc.not(this);
		} catch (InvalidOperandException e) {
			//this should never happen
			throw new UnexpectedInternalException(e);
		}
    }
    
    /**
     * Calculates the result of the inequality comparison of {@code this}
     * with another value.
     * 
     * @param param the {@link Primitive} representing the value to 
     *              which {@code this} must be compared.
     * @return a {@link Primitive} representing the result of the 
     *         operation.
     * @throws InvalidOperandException 
     * @throws InvalidTypeException 
     * @throws UnexpectedInternalException 
     */
    public Primitive ne(Primitive param)
    throws InvalidOperandException, InvalidTypeException, UnexpectedInternalException {
        return this.calc.ne(this, param);
    }
    
    /**
     * Calculates the result of the equality comparison of {@code this}
     * with another value.
     * 
     * @param param the {@link Primitive} representing the value to 
     *              which {@code this} must be compared.
     * @return a {@link Primitive} representing the result of the 
     *         operation.
     * @throws InvalidOperandException 
     * @throws InvalidTypeException 
     * @throws UnexpectedInternalException 
     */
    public Primitive eq(Primitive param)
    throws InvalidOperandException, InvalidTypeException, UnexpectedInternalException {
        return this.calc.eq(this, param);
    }
    
    /**
     * Calculates the result of the arithmetic greater-or-equal-than 
     * comparison of {@code this} with another value.
     * 
     * @param param the {@link Primitive} representing the value to 
     *              which {@code this} must be compared.
     * @return a {@link Primitive} representing the result of the 
     *         operation.
     * @throws InvalidOperandException 
     * @throws InvalidTypeException 
     * @throws UnexpectedInternalException 
     */
    public Primitive ge(Primitive param)
    throws InvalidOperandException, InvalidTypeException, UnexpectedInternalException {
        return this.calc.ge(this, param);
    }
    
    /**
     * Calculates the result of the arithmetic less-or-equal-than 
     * comparison of {@code this} with another value.
     * 
     * @param param the {@link Primitive} representing the value to 
     *              which {@code this} must be compared.
     * @return a {@link Primitive} representing the result of the 
     *         operation.
     * @throws InvalidTypeException 
     * @throws InvalidOperandException 
     * @throws UnexpectedInternalException 
     */
    public Primitive le(Primitive param)
    throws InvalidOperandException, InvalidTypeException, UnexpectedInternalException {
        return this.calc.le(this, param);
    }
    
    /**
     * Calculates the result of the arithmetic greater-than 
     * comparison of {@code this} with another value.
     * 
     * @param param the {@link Primitive} representing the value to 
     *              which {@code this} must be compared.
     * @return a {@link Primitive} representing the result of the 
     *         operation.
     * @throws InvalidTypeException 
     * @throws InvalidOperandException 
     * @throws UnexpectedInternalException 
     */
    public Primitive gt(Primitive param)
    throws InvalidOperandException, InvalidTypeException, UnexpectedInternalException {
        return this.calc.gt(this, param);
    }
    
    /**
     * Calculates the result of the arithmetic less-than 
     * comparison of {@code this} with another value.
     * 
     * @param param the {@link Primitive} representing the value to 
     *              which {@code this} must be compared.
     * @return a {@link Primitive} representing the result of the 
     *         operation.
     * @throws InvalidTypeException 
     * @throws InvalidOperandException 
     * @throws UnexpectedInternalException 
     */
    public Primitive lt(Primitive param)
    throws InvalidOperandException, InvalidTypeException, UnexpectedInternalException {
        return this.calc.lt(this, param);
    }
    
    /**
     * Calculates the result of the application of a unary {@link Operator} 
     * to {@code this}.
     * 
     * @param op the {@link Operator} to be applied. It must be
     *        unary.
     * @return a {@link Primitive} representing the result of the 
     *         operation.
	 * @throws InvalidOperatorException when {@code operator} is not unary.
     * @throws UnexpectedInternalException 
     */
    public Primitive applyUnary(Operator op) 
    throws InvalidOperatorException, InvalidOperandException, 
    InvalidTypeException, UnexpectedInternalException {
    	final Primitive retVal = this.calc.applyUnary(op, this);
    	return retVal;
    }
    
    /**
     * Calculates the result of the application of a binary {@link Operator} 
     * to {@code this} and another value.
     * 
     * @param op the {@link Operator} to be applied. It must be
     *        binary.
     * @param param the other {@link Primitive} to which {@code op}
     *        must be applied.
     * @return a {@link Primitive} representing the result of the 
     *         operation.
	 * @throws InvalidOperatorException  when {@code operator} is not binary.
     * @throws UnexpectedInternalException 
     */
    public Primitive applyBinary(Operator op, Primitive param) 
    throws InvalidOperatorException, InvalidOperandException, 
    InvalidTypeException, UnexpectedInternalException {
    	final Primitive retVal = this.calc.applyBinary(this, op, param);    	
    	return retVal;
    }
    
	/**
	 * Converts this value to another type.
	 * 
	 * @param type a {@code char} representing the type of the conversion.
	 * @param arg a {@link Primitive}.
	 * @return a {@link Primitive} representing the result of converting 
	 *         {@code arg} to {@code type}, or {@code arg} if it already
	 *         has type {@code type}.
	 * @throws InvalidTypeException when {@code arg} cannot be converted 
	 *         to {@code type}.
	 * @throws UnexpectedInternalException 
	 */
    public Primitive to(char type) 
    throws InvalidTypeException, UnexpectedInternalException {
    	try {
    		return this.calc.to(type, this);
    	} catch (InvalidOperandException e) {
    		//this should never happen
    		throw new UnexpectedInternalException(e);
    	}
    }
}