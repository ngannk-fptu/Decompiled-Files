/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.cache;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.annotation.SdkInternalApi;

@SdkInternalApi
public interface EndpointDiscoveryCacheLoader<K, V> {
    public V load(K var1, AmazonWebServiceRequest var2);
}

