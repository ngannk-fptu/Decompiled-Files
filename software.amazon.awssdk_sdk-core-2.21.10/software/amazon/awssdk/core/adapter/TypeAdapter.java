/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.core.adapter;

import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public interface TypeAdapter<SourceT, DestinationT> {
    public DestinationT adapt(SourceT var1);
}

