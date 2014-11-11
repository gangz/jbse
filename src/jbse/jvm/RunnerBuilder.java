package jbse.jvm;

import java.util.Map;

import jbse.exc.bc.InvalidClassFileFactoryClassException;
import jbse.exc.common.UnexpectedInternalException;
import jbse.exc.dec.DecisionException;
import jbse.exc.jvm.CannotBuildEngineException;
import jbse.exc.jvm.InitializationException;
import jbse.exc.jvm.NonexistingObservedVariablesException;

public class RunnerBuilder {
	/**
	 * Constructor.
	 */
	public RunnerBuilder() { }
	
	/** Used to build e. */
	private final EngineBuilder eb = new EngineBuilder();

	/** The {@link Engine} underlying the built {@link Runner}. */
	private Engine engine;

	/**
	 * Builds a {@link Runner}.
	 * 
	 * @param parameters the {@link RunnerParameters} to configure the {@link Runner}.
	 * 
	 * @return a {@link Runner}.
	 * @throws CannotBuildEngineException whenever {@code parameters} has
	 *         insufficient information for creating a {@link Runner}.
	 * @throws DecisionException in case initialization of the 
	 *         decision procedure fails for some reason.
	 * @throws InitializationException in case the specified root method 
	 *         does not exist or cannot be symbolically executed for 
	 *         any reason (e.g., is native).
	 * @throws InvalidClassFileFactoryClassException in case the class object 
	 *         provided to build a class file factory cannot be used
	 *         (e.g., it has not a suitable constructor or it is not visible).
	 * @throws NonexistingObservedVariablesException in case some of the provided 
	 *         observed variable names cannot be observed. This is the only exception
	 *         that allows nevertheless to perform symbolic execution, in which case 
	 *         only the observers to existing variables will be notified.
	 * @throws UnexpectedInternalException whenever some internal unexpected error occurs.
	 */
	public Runner build(RunnerParameters parameters) 
	throws CannotBuildEngineException, DecisionException, InitializationException, 
	InvalidClassFileFactoryClassException, NonexistingObservedVariablesException, 
	UnexpectedInternalException {
		this.engine = eb.build(parameters.getEngineParameters());
		final Map<String, Integer> heapScope = parameters.getHeapScope();
		return new Runner(engine, parameters.actions, parameters.identifierSubregion, 
				parameters.timeout, heapScope, parameters.depthScope, parameters.countScope);
	}
	
	/**
	 * Returns the {@link Engine} underlying the built {@link Runner}.
	 * 
	 * @return an {@link Engine}, or {@code null} if creation failed.
	 */
	public Engine getEngine() {
		return this.engine;
	}
}
