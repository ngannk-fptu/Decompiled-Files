/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.map.JsonSerializer
 *  org.codehaus.jackson.map.SerializerProvider
 */
package com.atlassian.migration.agent.dto.assessment;

import java.io.IOException;
import java.util.List;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

class DataScopesSerializer
extends JsonSerializer<List<?>> {
    DataScopesSerializer() {
    }

    public void serialize(List<?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartArray();
        value.stream().map(Object::toString).forEach(v -> {
            try {
                jgen.writeString(v);
            }
            catch (IOException e) {
                throw new IllegalArgumentException("Invalid value for serialization: " + v, e);
            }
        });
        jgen.writeEndArray();
    }
}

