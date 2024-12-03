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
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.ResponseConnControl;
import org.apache.hc.core5.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class H2ResponseConnControl
extends ResponseConnControl {
    @Override
    public void process(HttpResponse response, EntityDetails entity, HttpContext context) throws HttpException, IOException {
        Args.notNull(context, "HTTP context");
        ProtocolVersion ver = context.getProtocolVersion();
        if (ver.getMajor() < 2) {
            super.process(response, entity, context);
        }
    }
}

