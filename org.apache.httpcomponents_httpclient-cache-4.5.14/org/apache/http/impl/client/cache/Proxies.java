/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.methods.CloseableHttpResponse
 *  org.apache.http.util.Args
 */
package org.apache.http.impl.client.cache;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.cache.ResponseProxyHandler;
import org.apache.http.util.Args;

class Proxies {
    Proxies() {
    }

    public static CloseableHttpResponse enhanceResponse(HttpResponse original) {
        Args.notNull((Object)original, (String)"HTTP response");
        if (original instanceof CloseableHttpResponse) {
            return (CloseableHttpResponse)original;
        }
        return (CloseableHttpResponse)Proxy.newProxyInstance(ResponseProxyHandler.class.getClassLoader(), new Class[]{CloseableHttpResponse.class}, (InvocationHandler)new ResponseProxyHandler(original));
    }
}

