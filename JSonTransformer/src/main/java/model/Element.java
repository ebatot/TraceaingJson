/*****************************************************************************

* Copyright (c) 2015, 2018 CEA LIST, Edouard Batot

*

* All rights reserved. This program and the accompanying materials

* are made available under the terms of the Eclipse Public License 2.0

* which accompanies this distribution, and is available at

* https://www.eclipse.org/legal/epl-2.0/

*

* SPDX-License-Identifier: EPL-2.0

*

* Contributors:

* CEA LIST - Initial API and implementation

* Edouard Batot (UOC SOM) ebatot@uoc.edu 

*****************************************************************************/


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

	public String generateJSon() {
		String res = "{ "
				+ "\"id\": \"" 	 + ID    + "\", "
				+ "\"name\": \"" + name  + "\", "
				+ "\"type\": \"" + sysmlType  + "\","
				//D3 parameter
				+ "\"size\": 100,"
				+ "\"group\": "+ElementFactory.getGroup(this)+""
				+ "}";
		return res ;
	}
	
	public String generateTraceaJSon() {
		String res = "{ "
				+ "\"id\": \"" 	 + ID    + "\", "
				+ "\"name\": \"" + name  + "\", "
				+ "\"type\": \"" + sysmlType  + "\""
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
