/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.ContentType
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.HttpResponse
 *  org.apache.hc.core5.http.nio.AsyncEntityConsumer
 *  org.apache.hc.core5.http.nio.support.AbstractAsyncResponseConsumer
 *  org.apache.hc.core5.http.protocol.HttpContext
 */
package org.apache.hc.client5.http.async.methods;

import java.io.IOException;
import org.apache.hc.client5.http.async.methods.SimpleAsyncEntityConsumer;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.nio.AsyncEntityConsumer;
import org.apache.hc.core5.http.nio.support.AbstractAsyncResponseConsumer;
import org.apache.hc.core5.http.protocol.HttpContext;

public final class SimpleResponseConsumer
extends AbstractAsyncResponseConsumer<SimpleHttpResponse, byte[]> {
    SimpleResponseConsumer(AsyncEntityConsumer<byte[]> entityConsumer) {
        super(entityConsumer);
    }

    public static SimpleResponseConsumer create() {
        return new SimpleResponseConsumer((AsyncEntityConsumer<byte[]>)new SimpleAsyncEntityConsumer());
    }

    public void informationResponse(HttpResponse response, HttpContext context) throws HttpException, IOException {
    }

    protected SimpleHttpResponse buildResult(HttpResponse response, byte[] entity, ContentType contentType) {
        SimpleHttpResponse simpleResponse = SimpleHttpResponse.copy(response);
        if (entity != null) {
            simpleResponse.setBody(entity, contentType);
        }
        return simpleResponse;
    }
}

