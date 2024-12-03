/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.reference.Reference
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.map.SerializerProvider
 *  org.codehaus.jackson.map.ser.std.SerializerBase
 */
package com.atlassian.confluence.rest.serialization;

import com.atlassian.confluence.api.model.reference.Reference;
import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

final class ReferenceSerializer
extends SerializerBase<Reference> {
    ReferenceSerializer() {
        super(Reference.class);
    }

    public void serialize(Reference toSerialize, JsonGenerator jsonGen, SerializerProvider arg2) throws IOException {
        if (toSerialize.isExpanded()) {
            jsonGen.writeObject(toSerialize.get());
        } else {
            jsonGen.writeNull();
        }
        jsonGen.flush();
    }
}

