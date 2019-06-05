package com.example.util;

import java.io.File;
import java.io.FileInputStream;
/*
 * Util method for reading a JSON file into a JSON String
 * */
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

/**
 * @author clare
 *
 */
public class JSON_Reader {

	static String JSONString = "";
	private static JsonSchema jsonSchema = null;
	private final static JsonSchemaFactory factory = JsonSchemaFactory.byDefault();

	public static void loadSchema() {
		ObjectMapper mapper = new ObjectMapper();

		// Read the json schema
		File initialFile = new File("schema.json");
		InputStream schema;

		try {
			schema = new FileInputStream(initialFile);
			JsonNode schemaNode = mapper.readTree(schema);
			// Add the input.json as the schema to validate all inputs against
			jsonSchema = factory.getJsonSchema(schemaNode);
		} catch (Exception e) {
			System.out.println("Error loading the json schema");
		}
	}

	
	public static String readJSONFile() {
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader("usecase.json"));
			JSONObject jsonObject = (JSONObject) obj;
			JSONString = jsonObject.toJSONString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return JSONString;
	}

	public static void traverse(JsonNode node, int level) {
	      if (node.getNodeType() == JsonNodeType.ARRAY) {
	          traverseArray(node, level);
	      } else if (node.getNodeType() == JsonNodeType.OBJECT) {
	          traverseObject(node, level);
	      } else {
	         throw new RuntimeException("Not yet implemented");
	      }
	  }

	  private static void traverseObject(JsonNode node, int level) {
	      node.fieldNames().forEachRemaining((String fieldName) -> {
	          JsonNode childNode = node.get(fieldName);
	          printNode(childNode, fieldName, level);
	          //for nested object or arrays
	          if (traversable(childNode)) {
	              traverse(childNode, level + 1);
	          }
	      });
	  }

	  private static void traverseArray(JsonNode node, int level) {
	      for (JsonNode jsonArrayNode : node) {
	          printNode(jsonArrayNode, "arrayElement", level);
	          if (traversable(jsonArrayNode)) {
	              traverse(jsonArrayNode, level + 1);
	          }
	      }
	  }

	  private static boolean traversable(JsonNode node) {
	      return node.getNodeType() == JsonNodeType.OBJECT ||
	              node.getNodeType() == JsonNodeType.ARRAY;
	  }

	  private static void printNode(JsonNode node, String keyName, int level) {
			if (traversable(node)) {
				System.out.printf("%" + (level * 4 - 3) + "s|-- %s=%s type=%s%n", "", keyName, node.toString(),
						node.getNodeType());

			} else {
				Object value = null;
				if (node.isTextual()) {
					value = node.textValue();
				} else if (node.isNumber()) {
					value = node.numberValue();
				} // todo add more types
				System.out.printf("%" + (level * 4 - 3) + "s|-- %s=%s type=%s%n", "", keyName, value, node.getNodeType());
			}
		}
	  
	public static JsonNode validateAgainstSchema(String inputJson) {
		final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode output = null;

		try {
			// Read the JSON schema
			File initialFile = new File("schema.json");
			InputStream schema = new FileInputStream(initialFile);
			JsonNode schemaNode = mapper.readTree(schema);

			// Add the input.json as the schema to validate all inputs against
			final JsonSchema jsonSchema = factory.getJsonSchema(schemaNode);

			// Read the input JSON and assign it to a jsonNode
			JsonNode inputNode = mapper.readTree(inputJson);

			// Validate the input JSON against the schema
			ProcessingReport processingReport = jsonSchema.validate(inputNode);

			if (processingReport.isSuccess())
				return inputNode;
		} catch (Exception e) {

			return output;
		}
		return output;
	}
	
	public static JsonNode nodeFromString(String string) {

        if (string == null) return null;
        if (string.isEmpty()) string = "{}";
        try {
            return JsonLoader.fromString(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
