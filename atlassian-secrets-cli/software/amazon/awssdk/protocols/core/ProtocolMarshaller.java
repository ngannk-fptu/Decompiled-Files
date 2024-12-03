/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.core;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkPojo;

@SdkProtectedApi
public interface ProtocolMarshaller<MarshalledT> {
    public MarshalledT marshall(SdkPojo var1);
}

