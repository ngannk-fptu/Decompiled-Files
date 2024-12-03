/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http;

import org.apache.hc.core5.http.HttpResponse;

public interface HttpResponseFactory<T extends HttpResponse> {
    public T newHttpResponse(int var1, String var2);

    public T newHttpResponse(int var1);
}

