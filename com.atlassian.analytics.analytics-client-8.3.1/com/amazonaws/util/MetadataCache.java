/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import com.amazonaws.ResponseMetadata;
import com.amazonaws.annotation.SdkInternalApi;

@SdkInternalApi
public interface MetadataCache {
    public void add(Object var1, ResponseMetadata var2);

    public ResponseMetadata get(Object var1);
}

