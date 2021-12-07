package model;

import java.util.ArrayList;
import java.util.HashSet;

import transform.ElementFactory;

public class Element extends TracingElement {
	private  String name, qualifiedName;
	private  String sysmlType;
	
	private  ArrayList<Connection> sourceOf, targetOf; 
	
	public Element(String identifier) {
		super(identifier);
		sourceOf = new ArrayList<>(1);
		targetOf = new ArrayList<>(1);
	}
	
	@Override
	public boolean equals(Object obj) {
		//TODO ATTENTION !
		if(obj == null) return false;
		if(!obj.getClass().equals(this.getClass())) return false;
		return this.getName().equals(((Element)obj).getName());
	}


	/**
	 * 
	 * @param e
	 * @return
	 */
	public boolean connects(Element e) {
		for (Connection c : sourceOf)
			if (c.getSourceElements().contains(e) || c.getTargetElements().contains(e)) //contains(TracingElement) uses ID.
				return true;
		for (Connection c : targetOf)
			if (c.getSourceElements().contains(e) || c.getTargetElements().contains(e))
				return true;
		return false;
	}
	
	public HashSet<Connection> connections(Element e) {
		HashSet<Connection> res = new HashSet<>(1);
		for (Connection c : sourceOf)
			if (c.getSourceElements().contains(e) || c.getTargetElements().contains(e)) //contains(TracingElement) uses ID.
				res.add(c);
		for (Connection c : targetOf)
			if (c.getSourceElements().contains(e) || c.getTargetElements().contains(e))
				res.add(c);
		return res;
	}

	public HashSet<String> connectionTypes(Element e) {
		HashSet<String> res = new HashSet<>();
		for (Connection c : connections(e)) 
			res.addAll(c.getTracetypes());
		return res;
	}
	
	/**
	 * Indicates if the Element in paramteer connects with this. Comparison are made
	 * BY NAME !! (Prototype purpose)
	 * 
	 * @param e
	 * @return
	 */
	public boolean connectsByName(Element e) {
		for (Connection c : sourceOf) {
			for (Element e2 : c.getSourceElements()) {
				if (e2.getName().equals(e.getName()))
					return true;
			}
			for (Element e2 : c.getTargetElements()) {
				if (e2.getName().equals(e.getName()))
					return true;
			}
		}
		for (Connection c : targetOf) {
			for (Element e2 : c.getSourceElements()) {
				if (e2.getName().equals(e.getName()))
					return true;
			}
			for (Element e2 : c.getTargetElements()) {
				if (e2.getName().equals(e.getName()))
					return true;
			}
		}
		return false;
	}

	/**
	 * Indicates through which connections the Element in paramteer connects with
	 * this. <br/>
	 * ATTENTION ! Comparisons are made BY NAME !! (Prototype purpose)
	 * 
	 * @param e
	 * @return
	 */
	public HashSet<Connection> connectionsByName(Element e) {
		HashSet<Connection> res = new HashSet<>(1);
		for (Connection c : sourceOf) {
			for (Element e2 : c.getSourceElements()) {
				if (e2.getName().equals(e.getName()))
					res.add(c);
			}
			for (Element e2 : c.getTargetElements()) {
				if (e2.getName().equals(e.getName()))
					res.add(c);
			}
		}
		for (Connection c : targetOf) {
			for (Element e2 : c.getSourceElements()) {
				if (e2.getName().equals(e.getName()))
					res.add(c);
			}
			for (Element e2 : c.getTargetElements()) {
				if (e2.getName().equals(e.getName()))
					res.add(c);
			}
		}
		return res;
	}

	public HashSet<String> connectionByNameTypes(Element e) {
		HashSet<String> res = new HashSet<>();
		for (Connection c : connectionsByName(e))
			res.addAll(c.getTracetypes());
		return res;
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
	
	public void setSysmlType(String type) {
		this.sysmlType = type;
	}
	
	public String getName() {
		return name;
	}
	
	public String getQualifiedName() {
		return qualifiedName;
	}
	
	public String getSysmlType() {
		return sysmlType;
	}

	public String toStringJSon() {
		String res = "{ "
				+ "\"id\": \"" 	 + ElementFactory.getD3ID(this) + "\", "
				+ "\"name\": \"" + name  + "\", "
				+ "\"type\": \"" + sysmlType  + "\","
				//D3 parameter
				+ "\"size\": 100,"
				+ "\"group\": "+ElementFactory.getGroup(this)+""
				+ "}";
		return res ;
	}
	
	@Override
	public String toString() {
		return "<"+sysmlType+"::"+name+">";
	}

	public void addSource(Connection connection) {
		sourceOf.add(connection);
	}
	public void addTarget(Connection connection) {
		targetOf.add(connection);
	}
}
