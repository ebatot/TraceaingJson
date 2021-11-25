package model;

public class Element extends TracingElement {
	String name, qualifiedName;
	String type;
	
	public Element(String identifier) {
		super(identifier);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public String getQualifiedName() {
		return qualifiedName;
	}
	
	public String getType() {
		return type;
	}

	public String toStringJSon() {
		String res = "{ "
				+ "\"id\": \"" 	 + ID    + "\", "
				+ "\"name\": \"" + name  + "\", "
				+ "\"type\": \"" + type  + "\""
				+ "}";
		return res ;
	}
	
	@Override
	public String toString() {
		return "<"+type+"::"+name+">";
	}
}
