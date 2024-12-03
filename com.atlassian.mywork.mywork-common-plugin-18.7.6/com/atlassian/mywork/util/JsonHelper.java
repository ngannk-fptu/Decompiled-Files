/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.node.ObjectNode
 */
package com.atlassian.mywork.util;

import java.io.IOException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

public class JsonHelper {
    private JsonHelper() {
    }

    public static <T extends JsonNode> T copy(T node) {
        try {
            return (T)new ObjectMapper().readTree(node.traverse());
        }
        catch (IOException e) {
            throw new AssertionError((Object)e);
        }
    }

    public static ObjectNode parseObject(String json) {
        try {
            JsonNode node = new ObjectMapper().readTree(json);
            if (node.isObject()) {
                return (ObjectNode)node;
            }
            throw new IllegalArgumentException("'" + json + "' is not a JSON object");
        }
        catch (IOException e) {
            throw new IllegalArgumentException("'" + json + "' is not valid JSON: " + e.getMessage());
        }
    }
}

