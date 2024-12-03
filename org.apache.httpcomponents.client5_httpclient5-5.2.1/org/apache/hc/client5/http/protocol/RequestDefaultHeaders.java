/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.http.EntityDetails
 *  org.apache.hc.core5.http.Header
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.http.HttpRequestInterceptor
 *  org.apache.hc.core5.http.Method
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.client5.http.protocol;

import java.io.IOException;
import java.util.Collection;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Args;

@Contract(threading=ThreadingBehavior.STATELESS)
public class RequestDefaultHeaders
implements HttpRequestInterceptor {
    public static final RequestDefaultHeaders INSTANCE = new RequestDefaultHeaders();
    private final Collection<? extends Header> defaultHeaders;

    public RequestDefaultHeaders(Collection<? extends Header> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
    }

    public RequestDefaultHeaders() {
        this(null);
    }

    public void process(HttpRequest request, EntityDetails entity, HttpContext context) throws HttpException, IOException {
        Args.notNull((Object)request, (String)"HTTP request");
        String method = request.getMethod();
        if (Method.CONNECT.isSame(method)) {
            return;
        }
        if (this.defaultHeaders != null) {
            for (Header header : this.defaultHeaders) {
                if (request.containsHeader(header.getName())) continue;
                request.addHeader(header);
            }
        }
    }
}

