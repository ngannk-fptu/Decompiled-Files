/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.core.SdkPojo
 */
package software.amazon.awssdk.protocols.core;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkPojo;

@SdkProtectedApi
public interface ProtocolMarshaller<MarshalledT> {
    public MarshalledT marshall(SdkPojo var1);
}

