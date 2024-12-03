/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.client;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.http.HttpException;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.NHttpClientEventHandler;

class InternalRequestExecutor
implements NHttpClientEventHandler {
    private final Log log;
    private final NHttpClientEventHandler handler;

    public InternalRequestExecutor(Log log, NHttpClientEventHandler handler) {
        this.log = log;
        this.handler = handler;
    }

    @Override
    public void connected(NHttpClientConnection conn, Object attachment) throws IOException, HttpException {
        if (this.log.isDebugEnabled()) {
            this.log.debug(conn + ": Connected");
        }
        this.handler.connected(conn, attachment);
    }

    @Override
    public void closed(NHttpClientConnection conn) {
        if (this.log.isDebugEnabled()) {
            this.log.debug(conn + ": Disconnected");
        }
        this.handler.closed(conn);
    }

    @Override
    public void requestReady(NHttpClientConnection conn) throws IOException, HttpException {
        if (this.log.isDebugEnabled()) {
            this.log.debug(conn + " Request ready");
        }
        this.handler.requestReady(conn);
    }

    @Override
    public void inputReady(NHttpClientConnection conn, ContentDecoder decoder) throws IOException, HttpException {
        if (this.log.isDebugEnabled()) {
            this.log.debug(conn + " Input ready");
        }
        this.handler.inputReady(conn, decoder);
        if (this.log.isDebugEnabled()) {
            this.log.debug(conn + " " + decoder);
        }
    }

    @Override
    public void outputReady(NHttpClientConnection conn, ContentEncoder encoder) throws IOException, HttpException {
        if (this.log.isDebugEnabled()) {
            this.log.debug(conn + " Output ready");
        }
        this.handler.outputReady(conn, encoder);
        if (this.log.isDebugEnabled()) {
            this.log.debug(conn + " " + encoder);
        }
    }

    @Override
    public void responseReceived(NHttpClientConnection conn) throws HttpException, IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug(conn + " Response received");
        }
        this.handler.responseReceived(conn);
    }

    @Override
    public void timeout(NHttpClientConnection conn) throws HttpException, IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug(conn + " Timeout");
        }
        this.handler.timeout(conn);
    }

    @Override
    public void exception(NHttpClientConnection conn, Exception ex) {
        if (this.log.isDebugEnabled()) {
            this.log.debug(conn + " Exception", ex);
        }
        this.handler.exception(conn, ex);
    }

    @Override
    public void endOfInput(NHttpClientConnection conn) throws IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug(conn + " End of input");
        }
        this.handler.endOfInput(conn);
    }
}

