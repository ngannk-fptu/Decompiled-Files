/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol;

import com.amazonaws.Request;
import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.protocol.ProtocolMarshaller;

@SdkProtectedApi
public interface ProtocolRequestMarshaller<OrigRequest>
extends ProtocolMarshaller {
    public void startMarshalling();

    public Request<OrigRequest> finishMarshalling();
}

