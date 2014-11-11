package jbse.tree;

final class DecisionAlternativeAstoreOut extends DecisionAlternativeAstore {
	private static final String OUT_ID = "OUT";
	private static final int OUT_BN = 2;
	private static final int HASH_CODE = 961784543;
	
	public DecisionAlternativeAstoreOut(boolean isConcrete) {
		super(false, isConcrete, (isConcrete ? 1 : OUT_BN));
	}
	
	@Override
	public DecisionAlternativeAstoreOut mkNonconcrete() {
		return new DecisionAlternativeAstoreOut(false);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return HASH_CODE;
	}

	@Override
	public String toString() {
		return OUT_ID;
	}
}