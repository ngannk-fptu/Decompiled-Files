/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.codecs;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.impl.nio.codecs.AbstractMessageWriter;
import org.apache.http.message.LineFormatter;
import org.apache.http.nio.reactor.SessionOutputBuffer;
import org.apache.http.params.HttpParams;
import org.apache.http.util.CharArrayBuffer;

public class DefaultHttpResponseWriter
extends AbstractMessageWriter<HttpResponse> {
    @Deprecated
    public DefaultHttpResponseWriter(SessionOutputBuffer buffer, LineFormatter formatter, HttpParams params) {
        super(buffer, formatter, params);
    }

    public DefaultHttpResponseWriter(SessionOutputBuffer buffer, LineFormatter formatter) {
        super(buffer, formatter);
    }

    public DefaultHttpResponseWriter(SessionOutputBuffer buffer) {
        super(buffer, null);
    }

    @Override
    protected void writeHeadLine(HttpResponse message) throws IOException {
        CharArrayBuffer buffer = this.lineFormatter.formatStatusLine(this.lineBuf, message.getStatusLine());
        this.sessionBuffer.writeLine(buffer);
    }
}

