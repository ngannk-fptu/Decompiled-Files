/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.map.JsonSerializer
 *  org.codehaus.jackson.map.SerializerProvider
 */
package com.atlassian.confluence.ui.rest.json;

import com.atlassian.fugue.Option;
import java.io.IOException;
import java.util.Collections;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

@Deprecated
public class LegacyOptionSerializer
extends JsonSerializer<Option> {
    public void serialize(Option option, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        if (option.isEmpty()) {
            provider.defaultSerializeValue(Collections.emptyList(), jgen);
        } else {
            provider.defaultSerializeValue(Collections.singletonList(option.get()), jgen);
        }
    }

    public Class<Option> handledType() {
        return Option.class;
    }
}

