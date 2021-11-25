package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * An abstracted ConnectionUsage in SysMLv2 terminology.
 * 
 * @author Edouard
 *
 */
public class Connection {
	String ID;
	String effectiveName, qualifiedName;
	ArrayList<AnnotatingFeature> annotatingFeatures = new ArrayList<>();
	String sourceId, targetId;
	private Element sourceElement;
	private Element targetElement;
	
	
	
	private Connection(String identifier) {
		this.ID = identifier;
	}
	
	public static Connection createConnection(String identifier) {
		return new Connection(identifier);
	}
	
	
	public boolean addAnnotatingFeature(AnnotatingFeature af) {
		return annotatingFeatures.add(af);
	}
	
	public String toStringPretty() {
		String res  = "Con. "+effectiveName+": { \n";
		for (MetadataFeature mf : getMetadatas()) {
			res += "  " + mf.toStringPretty() +",\n";
		}
		return res += "}";
	}
	
	public String toStringPretty(String prefix) {
		String res = prefix + "Con. " + effectiveName + ": " + sourceElement + "->" + targetElement + " { \n";
		for (MetadataFeature mf : getMetadatas()) {
			res += prefix + "  " + mf.toStringPretty() + ",\n";
		}
		return res += prefix + "}";
	}

	
	public String toStringJSon() {
		String res = "{ "
				+ "\"id\": \""+ID+"\", "
				+ "\"name\": \""+effectiveName  +"\", "
				+ "\"type\": \""+getTracetype() +"\", "
				+ "\"source_id\": \""+sourceId  +"\", "
				+ "\"target_id\": \""+targetId  +"\", "
				+ "\"confidence\": "+getConfidenceValue()+""
				+ "}";
		return res;
	}
	
	
	public String toString() {
		return "<Con. "+ID+": AF("+annotatingFeatures.size()+")>";
	}
	
	public List<MetadataFeature> getMetadatas() {
		List<MetadataFeature> res = new ArrayList<>();
		for (AnnotatingFeature annotatingFeature : annotatingFeatures) {
			for (MetadataFeature mf : annotatingFeature.metadatas.values()) {
				res.add(mf);
			}
		}
		return res;
	}
	
	public HashMap<String, MetadataFeature> getMetadatasMap() {
		HashMap<String, MetadataFeature> res = new HashMap<>();
		for (AnnotatingFeature annotatingFeature : annotatingFeatures) {
			for (MetadataFeature mf : annotatingFeature.metadatas.values()) {
				res.put(mf.ID, mf);
			}
		}
		return res;
	}
	
	public HashMap<String, MetadataFeature> getMetadatas(String effectiveName) {
		HashMap<String, MetadataFeature> res = new HashMap<>();
		for (AnnotatingFeature annotatingFeature : annotatingFeatures) {
			for (MetadataFeature mf : annotatingFeature.metadatas.values()) {
				if( !mf.effectiveName.equals(effectiveName) )
					res.put(mf.ID, mf);
			}
		}
		return res;
	}
	
	public String getID() {
		return ID;
	}
	public String getEffectiveName() {
		return effectiveName;
	}
	
	public String getQualifiedName() {
		return qualifiedName;
	}
	
	public String getSourceId() {
		return sourceId;
	}
	
	public String getTargetId() {
		return targetId;
	}
	
	public void setEffectiveName(String effectiveName) {
		this.effectiveName = effectiveName;
	}
	
	public void setQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}
	
	public void setSourceId(String sourceID) {
		this.sourceId = sourceID;
	}
	
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	
	public static String UNTYPED = "Untyped";
	/**
	 * 
	 * @return ¡ Attention ! The first type found ¡ ATTENTION !.
	 * 
	 */
	public String getTracetype() {
		// @TODO make it clean with more types !
		if(getTracetypeValues().size() > 1) {
			String print = "Connection("+effectiveName+") has more than one type: " + getTracetypeValues();
			System.out.println("[Warning] "+print+" -> chosen:'"+getTracetypeValues().get(0)+"' (stack: Connection.getTraceType())");
			return getTracetypeValues().get(0);
		} else if (getTracetypeValues().size() == 1) {
			//One type only
			String res = getTracetypeValues().get(0);
			return res;
		} else return UNTYPED;
	}
	
	public List<String> getTracetypeValues() {
		List<String> res = new ArrayList<>();
		for (MetadataFeature mf : getMetadatas()) {
			if(mf.isTraceType())
				res.addAll(mf.getStringValues());
		}
		return res;
	}
	
	public Double getConfidenceValue() {
		for (MetadataFeature mf : getMetadatas()) {
			if(mf.isConfidence())
				return mf.getConfidence();
		}
		throw new UndefinedDataException("This connection has no confidence value defined.");
	}

	public void setSourceElement(Element buildElement) {
		this.sourceElement = buildElement;
		setSourceId(buildElement.getID());
	}
	
	public void setTargetElement(Element buildElement) {
		this.targetElement = buildElement;
		setTargetId(buildElement.getID());
	}
	
	public Element getSourceElement() {
		return sourceElement;
	}
	public Element getTargetElement() {
		return targetElement;
	}
}
