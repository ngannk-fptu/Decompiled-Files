/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.client;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.nio.DefaultNHttpClientConnection;
import org.apache.http.impl.nio.client.InternalRequestExecutor;
import org.apache.http.impl.nio.reactor.AbstractIODispatch;
import org.apache.http.nio.NHttpClientEventHandler;
import org.apache.http.nio.reactor.IOSession;

class InternalIODispatch
extends AbstractIODispatch<DefaultNHttpClientConnection> {
    private final Log log = LogFactory.getLog(InternalIODispatch.class);
    private final NHttpClientEventHandler handler;

    public InternalIODispatch(NHttpClientEventHandler handler) {
        this.handler = this.log.isDebugEnabled() ? new InternalRequestExecutor(this.log, handler) : handler;
    }

    @Override
    protected DefaultNHttpClientConnection createConnection(IOSession session) {
        this.log.debug("Unexpected invocation of #createConnection");
        session.close();
        throw new CancelledKeyException();
    }

    @Override
    protected void onConnected(DefaultNHttpClientConnection conn) {
        Object attachment = conn.getContext().getAttribute("http.session.attachment");
        try {
            this.handler.connected(conn, attachment);
        }
        catch (Exception ex) {
            this.handler.exception(conn, ex);
        }
    }

    @Override
    protected void onClosed(DefaultNHttpClientConnection conn) {
        this.handler.closed(conn);
    }

    @Override
    protected void onException(DefaultNHttpClientConnection conn, IOException ex) {
        this.handler.exception(conn, ex);
    }

    @Override
    protected void onInputReady(DefaultNHttpClientConnection conn) {
        conn.consumeInput(this.handler);
    }

    @Override
    protected void onOutputReady(DefaultNHttpClientConnection conn) {
        conn.produceOutput(this.handler);
    }

    @Override
    protected void onTimeout(DefaultNHttpClientConnection conn) {
        try {
            this.handler.timeout(conn);
        }
        catch (Exception ex) {
            this.handler.exception(conn, ex);
        }
    }
}

