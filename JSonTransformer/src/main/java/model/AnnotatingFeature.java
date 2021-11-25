package model;

import java.util.HashMap;

public class AnnotatingFeature extends TracingElement {
	HashMap<String, MetadataFeature> metadatas = new HashMap<>();
	
	public AnnotatingFeature(String ID) {
		super(ID);
	}
	
//	public AnnotatingFeature(String ID, String metadataEffectiveName, String metadataID) {
//		this.ID = ID;
//		MetadataFeature mf = new MetadataFeature(metadataID, metadataEffectiveName);
//		addMetatadataFeature(mf);
//	}
	
	public MetadataFeature addMetatadataFeature(MetadataFeature mf) {
		return metadatas.put(mf.effectiveName, mf);
	}
	
	public MetadataFeature addMetatadataFeature(String metadataEffectiveName, String metadataID) {
		MetadataFeature mf = new MetadataFeature(metadataID, metadataEffectiveName);
		return addMetatadataFeature(mf);
	}
	
	@Override
	public String toString() {
		return "<AF:"+ID+">";
	}
	public String toStringPretty() {
		String res =  "<AF:"+ID+ "{\n";
		for (MetadataFeature mf : metadatas.values()) {
			res += "    " + mf.toString() + ", \n";
		}
		return res +"  }";
	}
	
	public HashMap<String, MetadataFeature> getMetadatas() {
		return metadatas;
	}

}
