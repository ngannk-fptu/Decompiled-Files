/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.http.EntityDetails
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.HttpMessage
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.http.Method
 *  org.apache.hc.core5.http.ProtocolException
 *  org.apache.hc.core5.http.ProtocolVersion
 *  org.apache.hc.core5.http.message.MessageSupport
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.http.protocol.RequestContent
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.core5.http2.protocol;

import java.io.IOException;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpMessage;
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
    public static final H2RequestContent INSTANCE = new H2RequestContent();

    public H2RequestContent() {
    }

    public H2RequestContent(boolean overwrite) {
        super(overwrite);
    }

    public void process(HttpRequest request, EntityDetails entity, HttpContext context) throws HttpException, IOException {
        Args.notNull((Object)context, (String)"HTTP context");
        ProtocolVersion ver = context.getProtocolVersion();
        if (ver.getMajor() < 2) {
            super.process(request, entity, context);
        } else if (entity != null) {
            String method = request.getMethod();
            if (Method.TRACE.isSame(method)) {
                throw new ProtocolException("TRACE request may not enclose an entity");
            }
            MessageSupport.addContentTypeHeader((HttpMessage)request, (EntityDetails)entity);
            MessageSupport.addContentEncodingHeader((HttpMessage)request, (EntityDetails)entity);
            MessageSupport.addTrailerHeader((HttpMessage)request, (EntityDetails)entity);
        }
    }
}

