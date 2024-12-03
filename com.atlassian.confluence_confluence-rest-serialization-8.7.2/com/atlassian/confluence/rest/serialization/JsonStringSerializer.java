/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.JsonString
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.map.SerializerProvider
 *  org.codehaus.jackson.map.ser.std.SerializerBase
 */
package com.atlassian.confluence.rest.serialization;

import com.atlassian.confluence.api.model.JsonString;
import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

public class JsonStringSerializer
extends SerializerBase<JsonString> {
    public JsonStringSerializer() {
        super(JsonString.class);
    }

    public void serialize(JsonString value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeRawValue(value.getValue());
    }
}

