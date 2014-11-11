package jbse.rewr;

import jbse.exc.common.UnexpectedInternalException;
import jbse.exc.mem.InvalidOperandException;
import jbse.exc.mem.InvalidOperatorException;
import jbse.exc.mem.InvalidTypeException;
import jbse.exc.rewr.NoResultException;
import jbse.mem.Any;
import jbse.mem.Expression;
import jbse.mem.FunctionApplication;
import jbse.mem.NarrowingConversion;
import jbse.mem.Operator;
import jbse.mem.Primitive;
import jbse.mem.PrimitiveSymbolic;
import jbse.mem.PrimitiveVisitor;
import jbse.mem.Simplex;
import jbse.mem.Term;
import jbse.mem.WideningConversion;

/**
 * An abstract term rewriter for {@link Primitive}s.
 * 
 * @author Pietro Braione
 */
public class Rewriter implements Cloneable {
	private Primitive value;
	private RewriteVisitor visitor;
	
	protected CalculatorRewriting calc;
	
	public Rewriter() {
		this.calc = null;
		clear();
		this.visitor = new RewriteVisitor();
	}
	
	void setCalculator(CalculatorRewriting calc) {
		this.calc = calc;
	}

	protected final Primitive rewrite(Primitive p) throws NoResultException, UnexpectedInternalException {
		if (p == null || this.calc == null) {
			throw new NoResultException();
		} else {
			clear();
			try {
				p.accept(this.visitor);
			} catch (NoResultException | UnexpectedInternalException | RuntimeException e) {
				throw e;
			} catch (Exception e) {
				//this should never happen
				throw new UnexpectedInternalException(e);
			}
			return this.value;
		}
	}
	
	protected final void setResult(Primitive x) throws NoResultException {
		if (x == null) {
			throw new NoResultException();
		} else {
			this.value = x;
		}
	}
		
	protected final Primitive getResult() throws NoResultException {
		if (this.value == null) {
			throw new NoResultException();
		} else {
			return this.value;
		}
	}
		
	protected void rewriteAny(Any x) 
	throws NoResultException, UnexpectedInternalException {
		setResult(x);
	}
	
	protected void rewriteTerm(Term x) 
	throws NoResultException, UnexpectedInternalException {
		setResult(x);
	}
	
	protected void rewriteSimplex(Simplex x) 
	throws NoResultException, UnexpectedInternalException {
		setResult(x);
	}
	
	protected void rewritePrimitiveSymbolic(PrimitiveSymbolic x) 
	throws NoResultException, UnexpectedInternalException {
		setResult(x);
	}
	
	protected void rewriteFunctionApplication(FunctionApplication x) 
	throws NoResultException, UnexpectedInternalException {
		final Primitive[] args = x.getArgs();
		for (int i = 0; i < args.length; i++) {
			args[i] = rewrite(args[i]);
		}
		final FunctionApplication result;
		try {
			result = new FunctionApplication(x.getType(), this.calc, x.getOperator(), args);
		} catch (InvalidTypeException | InvalidOperandException e) {
			throw new NoResultException(e);
		}
		setResult(result);
	}

	protected void rewriteExpression(Expression x) 
	throws NoResultException, UnexpectedInternalException {
		final Operator operator = x.getOperator();
		final Expression result;
		try {
			if (x.isUnary()) {
				final Primitive operand = rewrite(x.getOperand());
				result = Expression.makeExpressionUnary(calc, operator, operand);
			} else {
				final Primitive firstOperand = rewrite(x.getFirstOperand());
				final Primitive secondOperand = rewrite(x.getSecondOperand());
				result = Expression.makeExpressionBinary(calc, firstOperand, operator, secondOperand);
			}
		} catch (InvalidTypeException | InvalidOperandException e) {
			//rewriting of operands yielded bad results: fails
			throw new NoResultException(e);
		} catch (InvalidOperatorException e) {
			//this should never happen
			throw new UnexpectedInternalException(e);
		}
		setResult(result);
	}
	
	protected void rewriteWideningConversion(WideningConversion x) 
	throws NoResultException, UnexpectedInternalException {
		final Primitive arg = rewrite(x.getArg());
		final WideningConversion result;
		try {
			result = WideningConversion.make(x.getType(), calc, arg);
		} catch (InvalidOperandException | InvalidTypeException e) {
			//this should never happen
			throw new UnexpectedInternalException(e);
		}
		setResult(result);
	}

	protected void rewriteNarrowingConversion(NarrowingConversion x) 
	throws NoResultException, UnexpectedInternalException {
		final Primitive arg = rewrite(x.getArg());
		final NarrowingConversion result;
		try {
			result = NarrowingConversion.make(x.getType(), calc, arg);
		} catch (InvalidOperandException | InvalidTypeException e) {
			//this should never happen
			throw new UnexpectedInternalException(e);
		}
		setResult(result);
	}

	private void clear() {
		this.value = null;
	}
	
	/**
	 * Just redispatches.
	 * 
	 * @author Pietro Braione
	 *
	 */
	private class RewriteVisitor implements PrimitiveVisitor {
		@Override public void visitAny(Any x) throws NoResultException, UnexpectedInternalException { Rewriter.this.rewriteAny(x); }

		@Override public void visitExpression(Expression e) throws NoResultException, UnexpectedInternalException { Rewriter.this.rewriteExpression(e); }

		@Override public void visitFunctionApplication(FunctionApplication x) throws NoResultException, UnexpectedInternalException { Rewriter.this.rewriteFunctionApplication(x); }

		@Override public void visitPrimitiveSymbolic(PrimitiveSymbolic s) throws NoResultException, UnexpectedInternalException { Rewriter.this.rewritePrimitiveSymbolic(s); }

		@Override public void visitSimplex(Simplex x) throws NoResultException, UnexpectedInternalException { Rewriter.this.rewriteSimplex(x); }

		@Override public void visitTerm(Term x) throws NoResultException, UnexpectedInternalException { Rewriter.this.rewriteTerm(x); }

		@Override public void visitWideningConversion(WideningConversion x) throws NoResultException, UnexpectedInternalException { Rewriter.this.rewriteWideningConversion(x); }

		@Override public void visitNarrowingConversion(NarrowingConversion x) throws NoResultException, UnexpectedInternalException { Rewriter.this.rewriteNarrowingConversion(x); }
	}
}
