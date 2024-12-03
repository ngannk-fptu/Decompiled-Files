/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonNode
 */
package com.atlassian.confluence.plugins.contentproperty.index.schema;

import org.codehaus.jackson.JsonNode;

class JsonField {
    private final JsonNode nodeValue;
    private final String fieldName;

    public JsonField(String fieldName, JsonNode nodeValue) {
        this.nodeValue = nodeValue;
        this.fieldName = fieldName;
    }

    public JsonNode getNodeValue() {
        return this.nodeValue;
    }

    public String getFieldName() {
        return this.fieldName;
    }
}

