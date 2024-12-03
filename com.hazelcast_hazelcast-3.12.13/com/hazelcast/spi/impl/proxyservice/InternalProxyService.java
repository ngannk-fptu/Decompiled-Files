/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.proxyservice;

import com.hazelcast.spi.ProxyService;

public interface InternalProxyService
extends ProxyService {
    public void destroyLocalDistributedObject(String var1, String var2, boolean var3);
}

