/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.NHttpServerConnection;
import org.apache.http.nio.NHttpServerEventHandler;
import org.apache.http.nio.NHttpServiceHandler;

@Deprecated
class NHttpServerEventHandlerAdaptor
implements NHttpServerEventHandler {
    private final NHttpServiceHandler handler;

    public NHttpServerEventHandlerAdaptor(NHttpServiceHandler handler) {
        this.handler = handler;
    }

    @Override
    public void connected(NHttpServerConnection conn) {
        this.handler.connected(conn);
    }

    @Override
    public void responseReady(NHttpServerConnection conn) throws IOException, HttpException {
        this.handler.responseReady(conn);
    }

    @Override
    public void requestReceived(NHttpServerConnection conn) throws IOException, HttpException {
        this.handler.requestReceived(conn);
    }

    @Override
    public void inputReady(NHttpServerConnection conn, ContentDecoder decoder) throws IOException, HttpException {
        this.handler.inputReady(conn, decoder);
    }

    @Override
    public void outputReady(NHttpServerConnection conn, ContentEncoder encoder) throws IOException, HttpException {
        this.handler.outputReady(conn, encoder);
    }

    @Override
    public void exception(NHttpServerConnection conn, Exception ex) {
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
    public void endOfInput(NHttpServerConnection conn) throws IOException {
        conn.close();
    }

    @Override
    public void timeout(NHttpServerConnection conn) {
        this.handler.timeout(conn);
    }

    @Override
    public void closed(NHttpServerConnection conn) {
        this.handler.closed(conn);
    }
}

