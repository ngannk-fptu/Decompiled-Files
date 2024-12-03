/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol.json.internal;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.protocol.MarshallingInfo;
import com.amazonaws.protocol.json.internal.JsonMarshallerContext;

@SdkInternalApi
public interface JsonMarshaller<T> {
    public static final JsonMarshaller<Void> NULL = new JsonMarshaller<Void>(){

        @Override
        public void marshall(Void val, JsonMarshallerContext context, MarshallingInfo marshallingInfo) {
        }
    };

    public void marshall(T var1, JsonMarshallerContext var2, MarshallingInfo<T> var3);
}

