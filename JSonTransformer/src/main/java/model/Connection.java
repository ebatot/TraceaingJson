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
public class Connection extends TracingElement {
	public static String UNTYPED = "Untyped";	
	
	private String effectiveName, qualifiedName;
	private ArrayList<AnnotatingFeature> annotatingFeatures = new ArrayList<>();
	private List<String> sourceIds, targetIds;
	private List<Element> sourceElements;
	private List<Element> targetElements;
	
	
	
	private Connection(String identifier) {
		super(identifier);
		this.sourceIds = new ArrayList<>(1);
		this.targetIds = new ArrayList<>(1);
		this.sourceElements = new ArrayList<>(1);
		this.targetElements = new ArrayList<>(1);
	}
	
	public static Connection createConnection(String identifier) {
		return new Connection(identifier);
	}
	
	public boolean connects(Connection c) {
		return 
				c.getSourceElements().equals(targetElements) ||
				c.getSourceElements().equals(sourceElements) ||
				c.getTargetElements().equals(targetElements) ||
				c.getTargetElements().equals(sourceElements) ;
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
	
	public String getEffectiveName() {
		return effectiveName;
	}
	
	public String getQualifiedName() {
		return qualifiedName;
	}
	
	public String getFirstSourceId() {
		return sourceIds.get(0);
	}

	public List<String> getSourceId() {
		return sourceIds;
	}

	public String getFirstTargetId() {
		return targetIds.get(0);
	}

	public List<String> getTargetId() {
		return targetIds;
	}
	
	public boolean addAnnotatingFeature(AnnotatingFeature af) {
		return annotatingFeatures.add(af);
	}

	public void setEffectiveName(String effectiveName) {
		this.effectiveName = effectiveName;
	}
	
	public void setQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}
	
	public void addSourceId(String sourceID) {
		this.sourceIds.add(sourceID);
	}
	
	public void addTargetId(String targetId) {
		this.targetIds.add(targetId);
	}
	
	public void addSourceElement(Element buildElement) {
		this.sourceElements.add(buildElement);
		addSourceId(buildElement.getID());
		buildElement.addSource(this);
	}
	
	public void addTargetElement(Element buildElement) {
		this.targetElements.add(buildElement);
		addTargetId(buildElement.getID());
		buildElement.addTarget(this);
	}
	
	public List<Element> getSourceElements() {
		return sourceElements;
	}
	
	public List<Element> getTargetElements() {
		return targetElements;
	}
	
	private Element getFirstSourceElement() {
		// TODO Auto-generated method stub
		return sourceElements.get(0);
	}

	private Element getFirstTargetElement() {
		// TODO Auto-generated method stub
		return targetElements.get(0);
	}

	/**
	 * 
	 * @return ¡ Attention ! The first type found ¡ ATTENTION !.
	 * 
	 */
	public String getFirstTracetype() {
		// @TODO make it clean with more types !
		if(getTracetypes().size() > 1) {
			String print = "Connection("+effectiveName+") has more than one type: " + getTracetypes();
			System.out.println("[Warning] "+print+" -> chosen:'"+getTracetypes().get(0)+"' (stack: Connection.getTraceType())");
			return getTracetypes().get(0);
		} else if (getTracetypes().size() == 1) {
			//One type only
			String res = getTracetypes().get(0);
			return res;
		} else return UNTYPED;
	}
	
	public List<String> getTracetypes() {
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

	public String toStringPretty() {
		return toStringPretty("");
	}
	
	public String toStringPretty(String prefix) {
		String res = prefix + "Con. " + effectiveName + ": " + getFirstSourceElement() + "->" + getFirstTargetElement() + " { \n";
		for (MetadataFeature mf : getMetadatas()) {
			res += prefix + "  " + mf.toStringPretty() + ",\n";
		}
		return res += prefix + "}";
	}
	
	public String toStringJSon() {
		String res = "{ "
				+ "\"id\": \""+ID+"\", "
				+ "\"name\": \""+effectiveName  +"\", "
				+ "\"type\": \""+getFirstTracetype() +"\", "
				+ "\"source_id\": \""+getFirstSourceId()  +"\", "
				+ "\"target_id\": \""+getFirstTargetId()  +"\", "
				+ "\"confidence\": "+getConfidenceValue()+""
				+ "}";
		return res;
	}
	
	public String toString() {
		return "<Con. "+ID+": AF("+annotatingFeatures.size()+")>";
	}
}


