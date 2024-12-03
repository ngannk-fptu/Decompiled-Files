/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.node.ArrayNode
 *  com.fasterxml.jackson.databind.node.ObjectNode
 */
package com.atlassian.confluence.plugins.gatekeeper.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.LinkedHashMap;
import java.util.Map;

public class FormValidator {
    private Map<String, String> errors = new LinkedHashMap<String, String>();

    public void addError(String key, String message) {
        this.errors.put(key, message);
    }

    public boolean isEmpty() {
        return this.errors.isEmpty();
    }

    public ObjectNode toJson() {
        ObjectMapper om = new ObjectMapper();
        ObjectNode result = om.createObjectNode();
        result.put("status", "invalid-data");
        ArrayNode errorArrayNode = result.putArray("errors");
        for (Map.Entry<String, String> entry : this.errors.entrySet()) {
            ObjectNode errorNode = errorArrayNode.addObject();
            errorNode.put("key", entry.getKey());
            errorNode.put("message", entry.getValue());
        }
        return result;
    }
}

