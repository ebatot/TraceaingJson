package transform;

import java.io.IOException;
import java.util.HashMap;

import model.Element;
import model.UndefinedDataException;

public class ElementFactory {
	String datamodel;
	
	public void setDatamodel(String datamodel) {
		this.datamodel = datamodel;
	}
	
	private HashMap<String, Element> allElements;

	public ElementFactory() {
		allElements = new HashMap<>();
	}
	
	
	public Element getElement(String id) {
		if(datamodel == null) 
			throw new UndefinedDataException("The datamodel has not been instantiated yet. \n"
					+ "Use 'setDatamodel' with a valid SysMLv2 model written in JSon to continue.");
		
		Element res = allElements.get(id);
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
				res.setType(JSonTransformer.oneValueJsonArrayToString(type));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			allElements.put(id, res);
		}
		return res;
	}


	static ElementFactory instance;
	public static ElementFactory getInstance() {
		if(instance == null)
			instance = new ElementFactory();
		return instance;
	}


	public String getDatamodel() {
		return datamodel;
	}
}
