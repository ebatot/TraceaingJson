package model;

import java.util.HashMap;

/**
 * An abstracted AnnotatingFeature in SysMLv2 terminology. The idea is to keep track of the intermediary AnnotatingFeature (and its ID) between a ConenctionUsage and a MeatadataFeature.
 * 
 * @author Edouard
 *
 */
public class AnnotatingFeature extends TracingElement {
	HashMap<String, MetadataFeature> metadatas = new HashMap<>();
	
	public AnnotatingFeature(String ID) {
		super(ID);
	}
	
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
