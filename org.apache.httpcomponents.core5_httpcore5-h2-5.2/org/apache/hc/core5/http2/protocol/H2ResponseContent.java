/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.http.EntityDetails
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.HttpMessage
 *  org.apache.hc.core5.http.HttpResponse
 *  org.apache.hc.core5.http.ProtocolVersion
 *  org.apache.hc.core5.http.message.MessageSupport
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.http.protocol.ResponseContent
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.core5.http2.protocol;

import java.io.IOException;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpMessage;
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

    public void process(HttpResponse response, EntityDetails entity, HttpContext context) throws HttpException, IOException {
        Args.notNull((Object)context, (String)"HTTP context");
        ProtocolVersion ver = context.getProtocolVersion();
        if (ver.getMajor() < 2) {
            super.process(response, entity, context);
        } else if (entity != null) {
            MessageSupport.addContentTypeHeader((HttpMessage)response, (EntityDetails)entity);
            MessageSupport.addContentEncodingHeader((HttpMessage)response, (EntityDetails)entity);
            MessageSupport.addTrailerHeader((HttpMessage)response, (EntityDetails)entity);
        }
    }
}

