/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol;

import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.protocol.MarshallingInfo;

@SdkProtectedApi
public interface ProtocolMarshaller {
    public <T> void marshall(T var1, MarshallingInfo<T> var2) throws SdkClientException;
}

