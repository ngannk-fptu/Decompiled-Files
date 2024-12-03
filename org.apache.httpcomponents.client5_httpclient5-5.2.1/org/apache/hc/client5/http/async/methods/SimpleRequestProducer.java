/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.http.nio.AsyncEntityProducer
 *  org.apache.hc.core5.http.nio.entity.BasicAsyncEntityProducer
 *  org.apache.hc.core5.http.nio.entity.StringAsyncEntityProducer
 *  org.apache.hc.core5.http.nio.support.BasicRequestProducer
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.client5.http.async.methods;

import org.apache.hc.client5.http.async.methods.SimpleBody;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.nio.AsyncEntityProducer;
import org.apache.hc.core5.http.nio.entity.BasicAsyncEntityProducer;
import org.apache.hc.core5.http.nio.entity.StringAsyncEntityProducer;
import org.apache.hc.core5.http.nio.support.BasicRequestProducer;
import org.apache.hc.core5.util.Args;

public final class SimpleRequestProducer
extends BasicRequestProducer {
    SimpleRequestProducer(SimpleHttpRequest request, AsyncEntityProducer entityProducer) {
        super((HttpRequest)request, entityProducer);
    }

    public static SimpleRequestProducer create(SimpleHttpRequest request) {
        Args.notNull((Object)request, (String)"Request");
        SimpleBody body = request.getBody();
        Object entityProducer = body != null ? (body.isText() ? new StringAsyncEntityProducer((CharSequence)body.getBodyText(), body.getContentType()) : new BasicAsyncEntityProducer(body.getBodyBytes(), body.getContentType())) : null;
        return new SimpleRequestProducer(request, (AsyncEntityProducer)entityProducer);
    }
}

