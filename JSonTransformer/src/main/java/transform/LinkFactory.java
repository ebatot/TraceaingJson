package transform;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.AnnotatingFeature;
import net.thisptr.jackson.jq.exception.JsonQueryException;

public class LinkFactory {

	
	public static List<String> getTracetypeFeaturesFromJSon(String strIn, AnnotatingFeature af)
			throws IOException, JsonQueryException, JsonProcessingException, JsonMappingException {
		String tmp2 = JSonTransformer.extractJSon(strIn,
				".[].payload "
						+ "| select((.AAAtype ==  \"MetadataFeature\") and "
						+ "((.owner.AAAid  == \""+af.getID()+"\") and"
						+ " (.effectiveName  == \"tracetype\"))"
						+ ") | .identifier"
				);
		List<String> linkmf2 = new ObjectMapper().readValue(tmp2, new TypeReference<List<String>>() {});
		return linkmf2;
	}


	public static List<String> getConfidenceFeaturesFromJSon(String strIn, AnnotatingFeature af)
			throws IOException, JsonQueryException, JsonProcessingException, JsonMappingException {
		String tmp1 = JSonTransformer.extractJSon(strIn,
				".[].payload "
						+ "| select((.AAAtype ==  \"MetadataFeature\") and "
						+ "((.owner.AAAid  == \""+af.getID()+"\") and"
						+ " (.effectiveName  == \"confidence\"))"
						+ ") | .identifier"
				);
		List<String> linkmf1 = new ObjectMapper().readValue(tmp1, new TypeReference<List<String>>() {});
		return linkmf1;
	}
	
	
	
	

}
