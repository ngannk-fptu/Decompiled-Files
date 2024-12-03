/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.concurrent.FutureCallback
 *  org.apache.hc.core5.http.ContentType
 *  org.apache.hc.core5.http.EntityDetails
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.HttpResponse
 *  org.apache.hc.core5.http.nio.AsyncResponseConsumer
 *  org.apache.hc.core5.http.nio.entity.AbstractBinDataConsumer
 *  org.apache.hc.core5.http.protocol.HttpContext
 */
package org.apache.hc.client5.http.async.methods;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.UnsupportedCharsetException;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
import org.apache.hc.core5.http.nio.entity.AbstractBinDataConsumer;
import org.apache.hc.core5.http.protocol.HttpContext;

public abstract class AbstractBinResponseConsumer<T>
extends AbstractBinDataConsumer
implements AsyncResponseConsumer<T> {
    private volatile FutureCallback<T> resultCallback;

    protected abstract void start(HttpResponse var1, ContentType var2) throws HttpException, IOException;

    protected abstract T buildResult();

    public void informationResponse(HttpResponse response, HttpContext context) throws HttpException, IOException {
    }

    public final void consumeResponse(HttpResponse response, EntityDetails entityDetails, HttpContext context, FutureCallback<T> resultCallback) throws HttpException, IOException {
        this.resultCallback = resultCallback;
        if (entityDetails != null) {
            try {
                ContentType contentType = ContentType.parse((CharSequence)entityDetails.getContentType());
                this.start(response, contentType != null ? contentType : ContentType.DEFAULT_BINARY);
            }
            catch (UnsupportedCharsetException ex) {
                throw new UnsupportedEncodingException(ex.getMessage());
            }
        } else {
            this.start(response, null);
            this.completed();
        }
    }

    protected final void completed() {
        this.resultCallback.completed(this.buildResult());
    }

    public void failed(Exception cause) {
    }
}

