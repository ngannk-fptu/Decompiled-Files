/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.ObjectCodec
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.JsonDeserializer
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.ObjectMapper
 */
package org.springframework.security.jackson2;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

class UnmodifiableMapDeserializer
extends JsonDeserializer<Map<?, ?>> {
    UnmodifiableMapDeserializer() {
    }

    public Map<?, ?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper)jp.getCodec();
        JsonNode node = (JsonNode)mapper.readTree(jp);
        LinkedHashMap result = new LinkedHashMap();
        if (node != null && node.isObject()) {
            Iterable fields = () -> ((JsonNode)node).fields();
            for (Map.Entry field : fields) {
                result.put(field.getKey(), mapper.readValue(((JsonNode)field.getValue()).traverse((ObjectCodec)mapper), Object.class));
            }
        }
        return Collections.unmodifiableMap(result);
    }
}

