package model;

import java.util.HashMap;
import java.util.HashSet;

public class MetadataFeature {
	String effectiveName;
	String ID;
	HashMap<String, Double> doubleValues = new HashMap<>();
	HashMap<String, String> stringValues = new HashMap<>();

	
	public MetadataFeature(String identifier, String effectiveName) {
		this.ID = identifier;
		this.effectiveName = effectiveName;
		effectiveNames.add(effectiveName);
	}
	
	@Override
	public String toString() {
		String doubleValue = doubleValues.isEmpty()? "" : ":" + doubleValues.values().toString();
		String stringValue = stringValues.isEmpty()? "" : ":" + stringValues.values().toString();
		return "<MF:"+effectiveName+":"+ID+":"+doubleValue+stringValue+">";
	}
	
	public void addDoubleValue(String literalRationalID, double value) {
		doubleValues.put(literalRationalID, value);
	}
	
	public HashMap<String, Double> getDoubleValues() {
		return doubleValues;
	}
	
	public void addStringValue(String literalRationalID, String value) {
		stringValues.put(literalRationalID, value);
	}
	
	public HashMap<String, String> getStringValues() {
		return stringValues;
	}
	public String getID() {
		return ID;
	}

	public String getEffectiveName() {
		return effectiveName;
	}
	
	private static HashSet<String> effectiveNames = new HashSet<>();
	public static HashSet<String> getEffectiveNames() {
		return effectiveNames;
	}

	public boolean isConfidence() {
		return effectiveName.equals("confidence");
	}
	
	public boolean isTraceType() {
		return effectiveName.equals("tracetype");
	}
}
