package my.projects.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Iterator;

public class JsonTest {
    private static String convertKey(String key, boolean format) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(key);
            ObjectNode ret = mapper.createObjectNode();
            if (format) {
                Iterator<String> it = node.fieldNames();
                while (it.hasNext()) {
                    String field = it.next();
                    if (field.startsWith("pk.")) {
                        ret.set(field.substring(3), node.get(field));
                    }
                }
            } else {
                ArrayNode array = (ArrayNode) node;
                Iterator<JsonNode> it = array.elements();
                while (it.hasNext()) {
                    JsonNode current = it.next();
                    if (current.isArray()) {
                        ArrayNode keys = (ArrayNode) current;
                        Iterator<JsonNode> itt = keys.elements();
                        while (itt.hasNext()) {
                            JsonNode cur = itt.next();
                            String fname = cur.fieldNames().next();
                            ret.set(fname, cur.get(fname));
                        }
                        break;
                    }
                }
            }
            return ret.toString();
        } catch (IOException e) {
            //log.error("convert key error for key string: '{}'", key, e);
        }
        return null;
    }

    public static void main(String[] args) {
        String array = "[\"test\",\"e2\",[{\"idda\":10},{\"id\":10.11}]]";
        String hash = "{\"database\":\"test\",\"table\":\"e2\",\"pk.idda\":10,\"pk.id\":10.11}";
        System.out.println(convertKey(array, false));
        System.out.println(convertKey(hash, true));
    }
}
