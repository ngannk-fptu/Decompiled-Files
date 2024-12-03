/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.protocol;

import java.io.IOException;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.message.MessageSupport;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.ResponseContent;
import org.apache.hc.core5.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class H2ResponseContent
extends ResponseContent {
    public static final H2ResponseContent INSTANCE = new H2ResponseContent();

    public H2ResponseContent() {
    }

    public H2ResponseContent(boolean overwrite) {
        super(overwrite);
    }

    @Override
    public void process(HttpResponse response, EntityDetails entity, HttpContext context) throws HttpException, IOException {
        Args.notNull(context, "HTTP context");
        ProtocolVersion ver = context.getProtocolVersion();
        if (ver.getMajor() < 2) {
            super.process(response, entity, context);
        } else if (entity != null) {
            MessageSupport.addContentTypeHeader(response, entity);
            MessageSupport.addContentEncodingHeader(response, entity);
            MessageSupport.addTrailerHeader(response, entity);
        }
    }
}

