/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http;

import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.protocol.HttpContext;

public interface HttpRequestMapper<T> {
    public T resolve(HttpRequest var1, HttpContext var2) throws HttpException;
}

