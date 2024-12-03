/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.map.SerializerProvider
 *  org.codehaus.jackson.map.ser.std.SerializerBase
 */
package com.atlassian.confluence.rest.serialization;

import com.atlassian.fugue.Option;
import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

@Deprecated
public class OptionSerializer
extends SerializerBase<Option> {
    public OptionSerializer() {
        super(Option.class);
    }

    public void serialize(Option option, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        if (option.isDefined()) {
            provider.defaultSerializeValue(option.get(), jgen);
        } else {
            jgen.writeNull();
        }
    }
}

