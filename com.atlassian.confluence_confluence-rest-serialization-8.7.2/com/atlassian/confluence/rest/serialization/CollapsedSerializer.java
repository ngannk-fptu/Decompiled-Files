/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.reference.Collapsed
 *  org.codehaus.jackson.JsonGenerationException
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.map.SerializerProvider
 *  org.codehaus.jackson.map.ser.std.SerializerBase
 */
package com.atlassian.confluence.rest.serialization;

import com.atlassian.confluence.api.model.reference.Collapsed;
import java.io.IOException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

class CollapsedSerializer
extends SerializerBase<Collapsed> {
    protected CollapsedSerializer() {
        super(Collapsed.class);
    }

    public void serialize(Collapsed value, JsonGenerator jsonGen, SerializerProvider provider) throws IOException, JsonGenerationException {
        jsonGen.writeNull();
    }
}

