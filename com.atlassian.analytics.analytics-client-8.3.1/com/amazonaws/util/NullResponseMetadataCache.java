/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import com.amazonaws.ResponseMetadata;
import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.util.MetadataCache;

@SdkInternalApi
public class NullResponseMetadataCache
implements MetadataCache {
    @Override
    public void add(Object obj, ResponseMetadata metadata) {
    }

    @Override
    public ResponseMetadata get(Object obj) {
        throw new SdkClientException("Response metadata caching is not enabled");
    }
}

