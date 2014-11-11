package jbse.exc.mem;

import jbse.exc.algo.CannotInvokeNativeException;
import jbse.mem.Value;

/**
 * Exception thrown when a {@link Value} cannot be represented metacircularly, 
 * because it does not exist (e.g., symbolic values) or it is to complex to 
 * do so (e.g., objects).
 * 
 * @author Pietro Braione
 *
 */
public class ValueDoesNotSupportNativeException extends CannotInvokeNativeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3772182382542414429L;

}
