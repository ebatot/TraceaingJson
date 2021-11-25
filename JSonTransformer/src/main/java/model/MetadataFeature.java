package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MetadataFeature {
	
	public static HashMap<MetadataFeature, Object> allMetadataFeatureValues = new HashMap<>();
	private static HashSet<String> allEffectiveNames = new HashSet<>();
	private static HashSet<String> allTracetypes = new HashSet<>();
	
	String effectiveName;
	String ID;
	HashMap<String, Double> doubleValues = new HashMap<>();
	HashMap<String, String> stringValues = new HashMap<>();

	
	public MetadataFeature(String identifier, String effectiveName) {
		this.ID = identifier;
		this.effectiveName = effectiveName;
		allEffectiveNames.add(effectiveName);
	}
	
	@Override
	public String toString() {
		String doubleValue = doubleValues.isEmpty()? "" : ":" + doubleValues.values().toString();
		String stringValue = stringValues.isEmpty()? "" : ":" + stringValues.values().toString();
		return "<MF:"+effectiveName+":"+ID+""+doubleValue+stringValue+">";
	}
	
	public String toStringPretty() {
		String doubleValue = doubleValues.isEmpty()? "" : ":" + doubleValues.values().toString();
		String stringValue = stringValues.isEmpty()? "" : ":" + stringValues.values().toString();
		return "<"+effectiveName+""+doubleValue+stringValue+">";
	}
	
	public void addDoubleValue(String literalRationalID, double value) {
		doubleValues.put(literalRationalID, value);
		allMetadataFeatureValues.put(this, value);
	}
	
	
	
	public void addStringValue(String literalRationalID, String value) {
		stringValues.put(literalRationalID, value);
		allMetadataFeatureValues.put(this, value);
		if(effectiveName.equals("tracetype"))
			allTracetypes.add(value);
	}
	
	/**
	 * 
	 * @return Map<ID of the literal, value of the literal>
	 */
	public HashMap<String, String> getStringValuesMap() {
		return stringValues;
	}
	
	public HashMap<String, Double> getDoubleValuesMap() {
		return doubleValues;
	}

	public ArrayList<String> getStringValues() {
		return new ArrayList<>(stringValues.values());
	}
	
	public ArrayList<Double> getDoubleValues() {
		return new ArrayList<>(doubleValues.values());
		
	}
	
	public String getID() {
		return ID;
	}

	public String getEffectiveName() {
		return effectiveName;
	}
	
	public static HashSet<String> getAllEffectiveNames() {
		return allEffectiveNames;
	}
	
	public static HashMap<MetadataFeature, Object> getAllMetadataFeatureValues(){
		return allMetadataFeatureValues;
	}
	public static HashSet<String> getAllTraceTypes(){
		return allTracetypes;
	}

	public boolean isConfidence() {
		return effectiveName.equals("confidence");
	}
	
	
	/**
	 * Return the FIRST confidence found. If more than one, ATTENTION !!
	 * @return
	 */
	public double getConfidence() {
		if(!isConfidence())
			throw new UndefinedDataException("This metdata feature is NOT about confidence.");
		try {
			if(getDoubleValues().size() > 1) {
				String print = "The metadataFeature has more than one confidence value: " + getDoubleValues();
				System.out.println("[Warning] "+print+" -> chosen:'"+getDoubleValues().get(0)+"' (stack: MetadataFeature.getConfidence())");
			}
			return getDoubleValues().get(0);
		} catch (Exception e) {
			throw new UndefinedDataException("There is not such a *confidence value* for this metadata feature.");
		}
	}
	
	public List<String> getTracetypes() {
		if(!isTraceType())
			throw new UndefinedDataException("This metdata feature is NOT about tracetype.");
		try {
			return getStringValues();
		} catch (Exception e) {
			throw new UndefinedDataException("There is not such *tracetype values* for this metdata feature.");
		}
	}
	
	public boolean isTraceType() {
		return effectiveName.equals("tracetype");
	}

//	public boolean isStringValue() {
//		return isTraceType();
//	}
}
