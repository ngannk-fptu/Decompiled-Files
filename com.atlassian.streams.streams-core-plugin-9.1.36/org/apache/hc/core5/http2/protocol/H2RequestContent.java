/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.protocol;

import java.io.IOException;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.message.MessageSupport;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.RequestContent;
import org.apache.hc.core5.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class H2RequestContent
extends RequestContent {
    public H2RequestContent() {
    }

    public H2RequestContent(boolean overwrite) {
        super(overwrite);
    }

    @Override
    public void process(HttpRequest request, EntityDetails entity, HttpContext context) throws HttpException, IOException {
        Args.notNull(context, "HTTP context");
        ProtocolVersion ver = context.getProtocolVersion();
        if (ver.getMajor() < 2) {
            super.process(request, entity, context);
        } else if (entity != null) {
            String method = request.getMethod();
            if (Method.TRACE.isSame(method)) {
                throw new ProtocolException("TRACE request may not enclose an entity");
            }
            MessageSupport.addContentTypeHeader(request, entity);
            MessageSupport.addContentEncodingHeader(request, entity);
            MessageSupport.addTrailerHeader(request, entity);
        }
    }
}

