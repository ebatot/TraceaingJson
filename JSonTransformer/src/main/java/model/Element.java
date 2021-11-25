package model;

import java.util.ArrayList;

public class Element extends TracingElement {
	String name, qualifiedName;
	String type;
	
	ArrayList<Connection> sourceOf, targetOf; 
	
	public Element(String identifier) {
		super(identifier);
		sourceOf = new ArrayList<>(1);
		targetOf = new ArrayList<>(1);
	}
	
	/**
	 * NOT TESTES !!! NOT TRIED !!
	 * @param e
	 * @return
	 */
	public boolean connects(Element e) {
		System.out.println("Element.connects()  NOT IMPLEMENTED");
		for (Connection cSource : sourceOf) {
			if(cSource.getSourceElement().equals(this))
				return true;
			if(cSource.getTargetElement().equals(this))
				return true;
		}
		return false;
	}
	
	public ArrayList<Connection> getSourceOf() {
		return sourceOf;
	}
	
	public ArrayList<Connection> getTargetOf() {
		return targetOf;
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

	public void addSource(Connection connection) {
		sourceOf.add(connection);
	}
	public void addTarget(Connection connection) {
		targetOf.add(connection);
	}
}
