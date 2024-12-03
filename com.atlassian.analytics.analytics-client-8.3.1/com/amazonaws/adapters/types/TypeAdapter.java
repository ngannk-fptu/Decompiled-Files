/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.adapters.types;

import com.amazonaws.annotation.SdkInternalApi;

@SdkInternalApi
public interface TypeAdapter<Source, Destination> {
    public Destination adapt(Source var1);
}

