/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.client.proxy;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.client.proxy.ViewProxy;

public interface ViewProxyProvider {
    public <T> ViewProxy<T> proxy(Client var1, Class<T> var2);
}

