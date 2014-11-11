package jbse.rules;

import jbse.mem.ReferenceSymbolic;

/**
 * The class for a null expansion constraint, filtering pattern for
 * possible origins of a {@link ReferenceSymbolic} that may be 
 * expanded to {@code null}.
 * 
 * @author Pietro Braione
 */
public class LICSRuleNull extends LICSRule {
	public LICSRuleNull(String originExp) {
		super(originExp);
	}

	@Override
	public String toString() {
		return this.originExp + " NULL";
	}
}