package model;

import java.util.ArrayList;
import java.util.HashMap;

public class Link {
	String ID;
	String effectiveName;
	ArrayList<AnnotatingFeature> annotatingFeatures = new ArrayList<>();
	
	
	public boolean addAnnotatingFeature(AnnotatingFeature af) {
		return annotatingFeatures.add(af);
	}
	
	public Link(String identifier) {
		this.ID = identifier;
	}
	
	
	public String toStringPretty() {
		String res  = "Link "+ID+": { \n";
		for (AnnotatingFeature af : annotatingFeatures) {
			res += "  " + af.toStringPretty() +",\n";
		}
		return res += "}";
	}
	
	public HashMap<String, MetadataFeature> getMetadatas() {
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
}
