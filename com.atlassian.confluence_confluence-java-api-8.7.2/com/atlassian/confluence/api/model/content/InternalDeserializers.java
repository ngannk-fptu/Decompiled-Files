/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.map.DeserializationContext
 *  org.codehaus.jackson.map.JsonDeserializer
 */
package com.atlassian.confluence.api.model.content;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.content.ContainerMap;
import com.atlassian.confluence.api.model.reference.Reference;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

@Internal
public class InternalDeserializers {

    static class ContainerMapDeserializer
    extends JsonDeserializer<Reference<ContainerMap>> {
        ContainerMapDeserializer() {
        }

        public Reference<ContainerMap> deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
            ContainerMap results = new ContainerMap();
            JsonNode node = jsonParser.readValueAsTree();
            Iterator it = node.getFields();
            while (it.hasNext()) {
                Map.Entry field = (Map.Entry)it.next();
                JsonNode fieldValue = (JsonNode)field.getValue();
                if (fieldValue.isObject()) continue;
                String key = (String)field.getKey();
                String value = fieldValue.asText();
                results.put(key, value);
            }
            return Reference.to(results);
        }
    }
}

