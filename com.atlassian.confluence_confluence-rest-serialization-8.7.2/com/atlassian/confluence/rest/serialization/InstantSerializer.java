/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonGenerationException
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.map.SerializerProvider
 *  org.codehaus.jackson.map.ser.std.SerializerBase
 */
package com.atlassian.confluence.rest.serialization;

import java.io.IOException;
import java.time.Instant;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

public class InstantSerializer
extends SerializerBase<Instant> {
    public InstantSerializer() {
        super(Instant.class);
    }

    public void serialize(Instant value, JsonGenerator jsonGen, SerializerProvider provider) throws IOException, JsonGenerationException {
        jsonGen.writeString(value.toString());
    }
}

