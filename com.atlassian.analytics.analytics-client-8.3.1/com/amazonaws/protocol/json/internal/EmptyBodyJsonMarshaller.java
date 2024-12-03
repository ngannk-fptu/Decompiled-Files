/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol.json.internal;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.protocol.json.StructuredJsonGenerator;

@SdkInternalApi
public interface EmptyBodyJsonMarshaller {
    public static final EmptyBodyJsonMarshaller NULL = new EmptyBodyJsonMarshaller(){

        @Override
        public void marshall(StructuredJsonGenerator generator) {
            generator.writeNull();
        }
    };
    public static final EmptyBodyJsonMarshaller EMPTY = new EmptyBodyJsonMarshaller(){

        @Override
        public void marshall(StructuredJsonGenerator generator) {
            generator.writeStartObject();
            generator.writeEndObject();
        }
    };

    public void marshall(StructuredJsonGenerator var1);
}

