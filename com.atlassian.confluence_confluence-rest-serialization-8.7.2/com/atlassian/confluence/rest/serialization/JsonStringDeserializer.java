/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.JsonString
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.map.DeserializationContext
 *  org.codehaus.jackson.map.JsonDeserializer
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.confluence.rest.serialization;

import com.atlassian.confluence.api.model.JsonString;
import java.io.IOException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonStringDeserializer
extends JsonDeserializer<JsonString> {
    private final ObjectMapper mapper = new ObjectMapper();

    public JsonString deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode jsonNode = parser.readValueAsTree();
        String value = this.mapper.writeValueAsString((Object)jsonNode);
        return new JsonString(value);
    }
}

