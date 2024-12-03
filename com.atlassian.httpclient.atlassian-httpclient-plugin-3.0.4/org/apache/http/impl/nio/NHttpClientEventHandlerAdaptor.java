/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.NHttpClientEventHandler;
import org.apache.http.nio.NHttpClientHandler;

@Deprecated
class NHttpClientEventHandlerAdaptor
implements NHttpClientEventHandler {
    private final NHttpClientHandler handler;

    public NHttpClientEventHandlerAdaptor(NHttpClientHandler handler) {
        this.handler = handler;
    }

    @Override
    public void connected(NHttpClientConnection conn, Object attachment) {
        this.handler.connected(conn, attachment);
    }

    @Override
    public void requestReady(NHttpClientConnection conn) throws IOException, HttpException {
        this.handler.requestReady(conn);
    }

    @Override
    public void responseReceived(NHttpClientConnection conn) throws IOException, HttpException {
        this.handler.responseReceived(conn);
    }

    @Override
    public void inputReady(NHttpClientConnection conn, ContentDecoder decoder) throws IOException, HttpException {
        this.handler.inputReady(conn, decoder);
    }

    @Override
    public void outputReady(NHttpClientConnection conn, ContentEncoder encoder) throws IOException, HttpException {
        this.handler.outputReady(conn, encoder);
    }

    @Override
    public void exception(NHttpClientConnection conn, Exception ex) {
        if (ex instanceof HttpException) {
            this.handler.exception(conn, (HttpException)ex);
        } else if (ex instanceof IOException) {
            this.handler.exception(conn, (IOException)ex);
        } else {
            if (ex instanceof RuntimeException) {
                throw (RuntimeException)ex;
            }
            throw new Error("Unexpected exception: ", ex);
        }
    }

    @Override
    public void endOfInput(NHttpClientConnection conn) throws IOException {
        conn.close();
    }

    @Override
    public void timeout(NHttpClientConnection conn) {
        this.handler.timeout(conn);
    }

    @Override
    public void closed(NHttpClientConnection conn) {
        this.handler.closed(conn);
    }
}

