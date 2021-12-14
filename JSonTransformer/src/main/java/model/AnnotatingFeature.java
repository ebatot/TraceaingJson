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
