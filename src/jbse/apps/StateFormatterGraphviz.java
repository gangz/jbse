package jbse.apps;

import java.util.Map;

import jbse.Type;
import jbse.bc.Signature;
import jbse.mem.Instance;
import jbse.mem.Klass;
import jbse.mem.Objekt;
import jbse.mem.Reference;
import jbse.mem.ReferenceConcrete;
import jbse.mem.ReferenceSymbolic;
import jbse.mem.State;

/**
 * A {@link StateFormatter} which renders just the heap of a {@link State} as a 
 * Graphviz DOT graph.
 * 
 * @author Pietro
 *
 */
public abstract class StateFormatterGraphviz implements StateFormatter {
	private static final String nullStyle = "[shape=invtriangle,label=\"null\",regular=true]";

	private int nextFreshNode;
	private boolean hasNull;
	private String currentNodePrefix;
	private String currentNodeName;
	private String currentNode;
	private String nullNodeName;
	private String nodes;
	private String edges;

	protected String formatOutput = "";
	
	public StateFormatterGraphviz() { }

	@Override
	public void format(State s) {
		this.formatOutput = "";
		this.formatOutput += "digraph \"" + s.getIdentifier() + "[" + s.getSequenceNumber() + "]\"" + " { ";
		this.formatOutput += this.formatHeap(s);
		//this.formatOutput += this.formatStaticMethodArea(s);
		this.formatOutput += "}";
	}
	

	@Override
	public void cleanup() {
		this.formatOutput = "";
	}
	

	public String formatHeap(State s) {
		final Map<Long, Objekt> h = s.getHeap();
		String retVal = ""; //= "subgraph cluster_heap { label=\"heap\" labeljust=l ";
		this.currentNodePrefix = "H";
		this.nodes = "";
		this.edges = "";
		this.nextFreshNode = 0;
		
		for (Map.Entry<Long, Objekt> e : h.entrySet()) {
			this.hasNull = false;
			this.nullNodeName = "";
			this.currentNodeName = this.currentNodePrefix + e.getKey(); 
			this.currentNode = this.currentNodeName + "[shape=box,label=\"" + e.getKey() + ":";
			this.currentNode += e.getValue().getType();
			this.formatObject(s, e.getValue());
			this.currentNode += "\"]";
			this.nodes += currentNode;
		}
		if (this.nodes.equals("")) {
			this.nodes += this.currentNodePrefix + "H[style=invis]"; //to force visualization of subgraph without nodes
		}
		retVal += this.nodes + this.edges; //+ "}";
		return retVal;
	}

	public String formatStaticMethodArea(State s) {
		final Map<String, Klass> a = s.getStaticMethodArea();
		String retVal = "subgraph cluster_staticstore { label=\"static store\" labeljust=l ";
		this.currentNodePrefix = "S";
		this.nodes = "";
		this.edges = "";

		for (Map.Entry<String, Klass> e : a.entrySet()) {
			this.hasNull = false;
			this.nullNodeName = "";
			this.currentNodeName = this.currentNodePrefix + e.getKey(); 
			this.currentNode = this.currentNodeName + "[shape=box,label=\"" + e.getKey() + ":";
			this.currentNode += e.getValue().getType();
			this.formatObject(s, e.getValue());
		}

		if (this.nodes.equals("")) {
			this.nodes += this.currentNodePrefix + "H[style=invis]"; //to force visualization of subgraph without nodes
		}
		retVal += this.nodes + this.edges + "}";
		return retVal;
	}
	
	public String formatObject(State s, Objekt o) {
		if (o instanceof Instance) {
			Instance i = (Instance) o;
			for (Signature sig : i.getFieldSignatures()) {
				if (Type.isArray(sig.getDescriptor()) ||
					Type.isReference(sig.getDescriptor())) {
					Reference r = (Reference) i.getFieldValue(sig);
					ReferenceSymbolic sr = null;
					if (r instanceof ReferenceSymbolic) {
						sr = (ReferenceSymbolic) r;
					}
					if (s.isNull(r)) {
						if (!this.hasNull) { 
							this.hasNull = true;
							this.nullNodeName = this.currentNodePrefix + "N" + this.nextFreshNode; 
							this.nodes += this.nullNodeName + nullStyle;
							this.nextFreshNode++;
						}
						edges += currentNodeName + "->" + nullNodeName;  
					} else if (sr == null) {
						edges += currentNodeName + "->" + "H" + ((ReferenceConcrete) r).getHeapPosition();
					} else if (s.resolved(sr)) {
						edges += currentNodeName + "->" + "H" + s.getResolution(sr);
					} else {
						String dummyNodeName = this.currentNodePrefix + "I" + this.nextFreshNode;
						nodes += dummyNodeName + "[label=\"?\" style=invis]";
						edges += this.currentNodeName + "->" + dummyNodeName;
						this.nextFreshNode++;
					}
					this.edges += "[label=\"" + sig.getName(); 
					if (sr != null) { 
						this.edges += " " + sr.getValue();
					}
					this.edges += "\"]";
				} else if (sig.getDescriptor().charAt(0) == Type.NULLREF) {
					if (!this.hasNull) { 
						this.hasNull = true;
						this.nullNodeName = this.currentNodePrefix + "N" + this.nextFreshNode; 
						this.nodes += this.nullNodeName + nullStyle;
						this.nextFreshNode++;
					}
					this.edges += this.currentNodeName + "->" + this.nullNodeName;
					this.edges += "[label=\"" + sig.getName() + "\"]";
				/*} else {
					this.currentNode += "\\n" + sig.getName() + " = " + i.getFieldValue(sig);*/
				}
			}
		} else { //is an array
			//TODO
		}
		return ""; //TODO
	}
}
