/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.ContentType
 *  org.apache.hc.core5.http.EntityDetails
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.http.HttpResponse
 *  org.apache.hc.core5.http.nio.AsyncPushConsumer
 *  org.apache.hc.core5.http.nio.entity.AbstractBinDataConsumer
 *  org.apache.hc.core5.http.protocol.HttpContext
 */
package org.apache.hc.client5.http.async.methods;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.UnsupportedCharsetException;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.entity.AbstractBinDataConsumer;
import org.apache.hc.core5.http.protocol.HttpContext;

public abstract class AbstractBinPushConsumer
extends AbstractBinDataConsumer
implements AsyncPushConsumer {
    protected abstract void start(HttpRequest var1, HttpResponse var2, ContentType var3) throws HttpException, IOException;

    public final void consumePromise(HttpRequest promise, HttpResponse response, EntityDetails entityDetails, HttpContext context) throws HttpException, IOException {
        if (entityDetails != null) {
            ContentType contentType;
            try {
                contentType = ContentType.parse((CharSequence)entityDetails.getContentType());
            }
            catch (UnsupportedCharsetException ex) {
                throw new UnsupportedEncodingException(ex.getMessage());
            }
            this.start(promise, response, contentType != null ? contentType : ContentType.DEFAULT_BINARY);
        } else {
            this.start(promise, response, null);
            this.completed();
        }
    }

    public void failed(Exception cause) {
    }
}

