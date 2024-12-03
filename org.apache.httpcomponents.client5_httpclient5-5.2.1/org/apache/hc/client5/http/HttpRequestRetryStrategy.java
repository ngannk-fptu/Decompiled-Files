/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.http.HttpResponse
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.util.TimeValue
 */
package org.apache.hc.client5.http;

import java.io.IOException;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;

@Contract(threading=ThreadingBehavior.STATELESS)
public interface HttpRequestRetryStrategy {
    public boolean retryRequest(HttpRequest var1, IOException var2, int var3, HttpContext var4);

    public boolean retryRequest(HttpResponse var1, int var2, HttpContext var3);

    default public TimeValue getRetryInterval(HttpRequest request, IOException exception, int execCount, HttpContext context) {
        return TimeValue.ZERO_MILLISECONDS;
    }

    public TimeValue getRetryInterval(HttpResponse var1, int var2, HttpContext var3);
}

