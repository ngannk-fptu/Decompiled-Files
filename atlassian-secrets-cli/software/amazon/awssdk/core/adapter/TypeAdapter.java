/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.adapter;

import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public interface TypeAdapter<SourceT, DestinationT> {
    public DestinationT adapt(SourceT var1);
}

