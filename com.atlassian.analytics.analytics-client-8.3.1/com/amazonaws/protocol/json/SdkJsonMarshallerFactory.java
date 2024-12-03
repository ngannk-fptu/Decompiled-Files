/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol.json;

import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.protocol.json.StructuredJsonGenerator;

@Deprecated
@SdkProtectedApi
public interface SdkJsonMarshallerFactory {
    public StructuredJsonGenerator createGenerator();

    public String getContentType();
}

