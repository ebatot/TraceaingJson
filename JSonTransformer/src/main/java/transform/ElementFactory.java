package transform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import model.Element;
import model.UndefinedDataException;

public class ElementFactory {
	String datamodel;
	
	public void setDatamodel(String datamodel) {
		this.datamodel = datamodel;
	}
	
	private HashMap<String, Element> elementsMap;

	public ElementFactory() {
		elementsMap = new HashMap<>();
	}
	
	public HashMap<String, Element> getElementsMap() {
		return elementsMap;
	}
	
	public List<Element> getAllElements() {
		ArrayList<Element> res = new ArrayList<>(elementsMap.values());
		Collections.sort(res, new Comparator<Element>() {
			@Override
			public int compare(Element o1, Element o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return res;
	}
	
	public Element getElement(String id) {
		if(datamodel == null) 
			throw new UndefinedDataException("The datamodel has not been instantiated yet. \n"
					+ "Use 'setDatamodel' with a valid SysMLv2 model written in JSon to continue.");
		
		Element res = elementsMap.get(id);
		if(res != null)
			return res;
		else {
			res = new Element(id);
			try {
				//Reduces the size of the datamodel to precess in the following three calls.
				String lr_s = JSonTransformer.getElementRawJsonFromID(datamodel, id);
				
				String name = JSonTransformer.getElementSpecificFieldFromID(lr_s, id, ".payload.name");
				res.setName(JSonTransformer.oneValueJsonArrayToString(name));
				String qname = JSonTransformer.getElementSpecificFieldFromID(lr_s, id, ".payload.qualifiedName");
				res.setQualifiedName(JSonTransformer.oneValueJsonArrayToString(qname));
				String type = JSonTransformer.getElementSpecificFieldFromID(lr_s, id, ".payload.AAAtype");
				res.setSysmlType(JSonTransformer.oneValueJsonArrayToString(type));

				
				/* Differentiate cases based on type */
				if(type.contains("Feature")) {
					List<String> inhFeatures_id = getIdsInField(datamodel, id, ".payload.inheritedFeature");
					if(inhFeatures_id.size() > 0) {
						String inhFeature_s = JSonTransformer.getElementRawJsonFromID(datamodel, inhFeatures_id.get(0)); 
						name = JSonTransformer.getElementSpecificFieldFromID(inhFeature_s, inhFeatures_id.get(0), ".payload.name");
						name = JSonTransformer.oneValueJsonArrayToString(name);
//						System.out.println("ElementFactory.getElement() inheritedFeature FOUND " + name + ": " + JSonTransformer.getElementSpecificFieldFromID(inhFeature_s, inhFeatures_id.get(0), ".payload.AAAtype"));
						res.setName(name);
						
					} else {
						
					}
//					System.out.println();
//					System.out.println();
					
					List<String> inhMembership_id = getIdsInField(datamodel, id, ".payload.inheritedMembership");
					if(inhMembership_id.size() > 0) {
						for (int i = 0; i < inhMembership_id.size(); i++) {
//							System.out.print("InhMermbership");
//							System.out.println(JSonTransformer.getElementRawJsonFromID(datamodel, inhMembership_id.get(i)));
						}
					}
					
					List<String> chainingFeature_id = getIdsInField(datamodel, id, ".payload.chainingFeature");
//					System.out.println(chainingFeature_id);
					if(chainingFeature_id.size() > 0) {
						String tmpName = "";
						name = "";
						for (int i = 0; i < chainingFeature_id.size(); i++) {
//							System.out.print("ChainingFeature");
//							System.out.println(JSonTransformer.getElementRawJsonFromID(datamodel, chainingFeature_id.get(i)));
							
							String jsonCF = JSonTransformer.getElementRawJsonFromID(datamodel, chainingFeature_id.get(i)); 
							tmpName = JSonTransformer.getElementSpecificFieldFromID(jsonCF, chainingFeature_id.get(i), ".payload.name");
							tmpName = JSonTransformer.oneValueJsonArrayToString(tmpName);
//							System.out.println("ElementFactory.getElement() ChainingFeature FOUND " + tmpName + ": " + JSonTransformer.getElementSpecificFieldFromID(jsonCF, chainingFeature_id.get(i), ".payload.AAAtype"));
							name += tmpName + ".";
						}
						res.setName(name.substring(0, name.length()-1));
					}
					
//					System.out.println(getIdsInField(datamodel, id, ".payload.inheritedFeature"));
//					System.out.println(lr_s);
//					System.exit(0);
				}			
			
			
			} catch (IOException e) {
				e.printStackTrace();
			}
			addGroup(res.getSysmlType());
			addElement(res);
			
		}
		return res;
	}
	
	/**
	 * @param res
	 */
	private void addElement(Element res) {
		elementsMap.put(res.getID(), res);
	}

	/**
	 * return the list of IDs of a specific field (potentially an array [{"AAAid": "value"}...]) of an identified element.
	 * 
	 * @param datamodel
	 * @param id
	 * @param field
	 * @return
	 */
	public static List<String> getIdsInField(String datamodel, String id, String field) {
		ArrayList<String> res = new ArrayList<>();
		try {
			String lr_s = JSonTransformer.getElementRawJsonFromID(datamodel, id);
			String strField = JSonTransformer.getElementSpecificFieldFromID(lr_s, id, field);
			strField = strField.substring(1, strField.length() - 1).trim();
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonElement jelem = gson.fromJson(strField, JsonElement.class);
			if(jelem.isJsonArray()) {
				JsonArray jobj = jelem.getAsJsonArray();
				for (int i = 0; i < jobj.size(); i++) {
					JsonObject jo = jobj.get(i).getAsJsonObject();
					res.add(jo.get("AAAid").getAsString());
				}
			} else if(jelem.isJsonObject()) {
				res.add(jelem.getAsString());
			} else if(jelem.isJsonPrimitive()) {
				res.add(jelem.getAsString());
			} else if(jelem.isJsonNull()) {
//				System.out.println("No element to add.");
			} else {
				System.out.println("ElementFactory.getIdsInField() Something went wrong.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

public static String showFieldsFromID(String datamodel, String id, String... fields) {
	String res = "'"+id+"':\n";
	try {
		String lr_s = JSonTransformer.getElementRawJsonFromID(datamodel, id);
		for (String field : fields) {
			String strField = JSonTransformer.getElementSpecificFieldFromID(lr_s, id, field);
			strField = strField.substring(1, strField.length() - 1).trim();
//			strField = strField.substring(1, strField.length() - 1).trim();

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonElement jelem = gson.fromJson(strField, JsonElement.class);
			if(jelem.isJsonArray()) {
				JsonArray jobj = jelem.getAsJsonArray();
				for (int i = 0; i < jobj.size(); i++) {
					JsonObject jo = jobj.get(i).getAsJsonObject();
					System.out.println(jo.get("AAAid") + "     ******");
				}
				
				
				JsonElement elt_con = jobj.getAsJsonArray();
	//			JsonElement elt_con = jobj.get("AAAid");
				System.out.println(elt_con+"     -----");
				
//				String lr_s2 = JSonTransformer.getElementRawJsonFromID(datamodel, id);
			
			
			}
			
			res += " - " + field + ": '" + strField + "'\n";
		}
	} catch (IOException e) {
		res += "SOMETHING WENT WRONG !\n";
		e.printStackTrace();
	}
	System.out.println(res);
	return res;
}

	static ElementFactory instance;
	public static ElementFactory getInstance() {
		if(instance == null)
			instance = new ElementFactory();
		return instance;
	}

	
	public static int getGroup(Element e) {
		return groups.get(e.getSysmlType());
	}
	
	private static void addGroup(String s) {
		if(groups.get(s) == null) {
			groups.put(s, countGroup++);
		}
	}

	static int countGroup = 0;
	public static HashMap<String, Integer> groups = new HashMap<>();


	public String getDatamodel() {
		return datamodel;
	}
}
