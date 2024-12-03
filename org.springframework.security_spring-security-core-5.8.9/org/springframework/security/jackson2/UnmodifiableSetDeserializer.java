/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.JsonProcessingException
 *  com.fasterxml.jackson.core.ObjectCodec
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.JsonDeserializer
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.node.ArrayNode
 */
package org.springframework.security.jackson2;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class UnmodifiableSetDeserializer
extends JsonDeserializer<Set> {
    UnmodifiableSetDeserializer() {
    }

    public Set deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = (ObjectMapper)jp.getCodec();
        JsonNode node = (JsonNode)mapper.readTree(jp);
        HashSet<Object> resultSet = new HashSet<Object>();
        if (node != null) {
            if (node instanceof ArrayNode) {
                ArrayNode arrayNode = (ArrayNode)node;
                for (JsonNode elementNode : arrayNode) {
                    resultSet.add(mapper.readValue(elementNode.traverse((ObjectCodec)mapper), Object.class));
                }
            } else {
                resultSet.add(mapper.readValue(node.traverse((ObjectCodec)mapper), Object.class));
            }
        }
        return Collections.unmodifiableSet(resultSet);
    }
}

