package model;

import java.util.ArrayList;
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
	String effectiveName;
	ArrayList<AnnotatingFeature> annotatingFeatures = new ArrayList<>();
	String sourceID, targetId;
	
	
	public boolean addAnnotatingFeature(AnnotatingFeature af) {
		return annotatingFeatures.add(af);
	}
	
	public Connection(String identifier) {
		this.ID = identifier;
	}
	
	
	public String toStringPretty() {
		String res  = "Con. "+effectiveName+": { \n";
		for (MetadataFeature mf : getMetadatas()) {
			res += "  " + mf.toStringPretty() +",\n";
		}
		return res += "}";
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
	
	public void setEffectiveName(String effectiveName) {
		this.effectiveName = effectiveName;
	}
	
	public void setSourceId(String sourceID) {
		this.sourceID = sourceID;
	}
	
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
}
