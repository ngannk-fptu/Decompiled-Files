/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.protocol;

import java.io.IOException;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.HttpDateGenerator;
import org.apache.hc.core5.util.Args;

@Contract(threading=ThreadingBehavior.SAFE)
public class RequestDate
implements HttpRequestInterceptor {
    public static final HttpRequestInterceptor INSTANCE = new RequestDate();

    @Override
    public void process(HttpRequest request, EntityDetails entity, HttpContext context) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        if (entity != null && !request.containsHeader("Date")) {
            request.setHeader("Date", HttpDateGenerator.INSTANCE.getCurrentDate());
        }
    }
}

