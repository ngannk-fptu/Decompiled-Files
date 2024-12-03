/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.map.DeserializationContext
 *  org.codehaus.jackson.map.JsonDeserializer
 *  org.codehaus.jackson.type.TypeReference
 */
package com.atlassian.confluence.api.serialization;

import com.atlassian.confluence.api.model.content.Label;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import java.io.IOException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.type.TypeReference;

public class MetadataValueDeserializer
extends JsonDeserializer<Object> {
    public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (jp.getCurrentName().equals("labels")) {
            return jp.readValueAs((TypeReference)new TypeReference<PageResponseImpl<Label>>(){});
        }
        return jp.readValueAs(Object.class);
    }
}

