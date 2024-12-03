/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.map.SerializerProvider
 *  org.codehaus.jackson.map.ser.std.SerializerBase
 */
package com.atlassian.confluence.rest.serialization;

import java.io.IOException;
import java.util.Optional;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

public class OptionalSerializer
extends SerializerBase<Optional> {
    public OptionalSerializer() {
        super(Optional.class);
    }

    public void serialize(Optional option, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        if (option.isPresent()) {
            provider.defaultSerializeValue(option.get(), jgen);
        } else {
            jgen.writeNull();
        }
    }
}

