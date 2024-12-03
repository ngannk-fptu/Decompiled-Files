/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol.json;

import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.protocol.json.StructuredJsonGenerator;

@SdkProtectedApi
public interface StructuredJsonMarshaller<T> {
    public void marshall(T var1, StructuredJsonGenerator var2);
}

