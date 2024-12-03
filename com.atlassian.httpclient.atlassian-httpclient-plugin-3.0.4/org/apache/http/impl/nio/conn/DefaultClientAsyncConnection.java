/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.conn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.impl.nio.DefaultNHttpClientConnection;
import org.apache.http.impl.nio.conn.LoggingIOSession;
import org.apache.http.nio.conn.ClientAsyncConnection;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.params.HttpParams;

@Deprecated
public class DefaultClientAsyncConnection
extends DefaultNHttpClientConnection
implements ClientAsyncConnection {
    private final Log headerLog = LogFactory.getLog("org.apache.http.headers");
    private final Log wireLog = LogFactory.getLog("org.apache.http.wire");
    private final Log log;
    private final String id;
    private IOSession original;

    public DefaultClientAsyncConnection(String id, IOSession ioSession, HttpResponseFactory responseFactory, ByteBufferAllocator allocator, HttpParams params) {
        super(ioSession, responseFactory, allocator, params);
        this.id = id;
        this.original = ioSession;
        this.log = LogFactory.getLog(ioSession.getClass());
        if (this.log.isDebugEnabled() || this.wireLog.isDebugEnabled()) {
            this.bind(new LoggingIOSession(ioSession, this.id, this.log, this.wireLog));
        }
    }

    @Override
    public void upgrade(IOSession ioSession) {
        this.original = ioSession;
        if (this.log.isDebugEnabled() || this.wireLog.isDebugEnabled()) {
            this.log.debug(this.id + " Upgrade session " + ioSession);
            this.bind(new LoggingIOSession(ioSession, this.id, this.headerLog, this.wireLog));
        } else {
            this.bind(ioSession);
        }
    }

    @Override
    public IOSession getIOSession() {
        return this.original;
    }

    public String getId() {
        return this.id;
    }

    @Override
    protected void onResponseReceived(HttpResponse response) {
        if (response != null && this.headerLog.isDebugEnabled()) {
            Header[] headers;
            this.headerLog.debug(this.id + " << " + response.getStatusLine().toString());
            for (Header header : headers = response.getAllHeaders()) {
                this.headerLog.debug(this.id + " << " + header.toString());
            }
        }
    }

    @Override
    protected void onRequestSubmitted(HttpRequest request) {
        if (request != null && this.headerLog.isDebugEnabled()) {
            Header[] headers;
            this.headerLog.debug(this.id + " >> " + request.getRequestLine().toString());
            for (Header header : headers = request.getAllHeaders()) {
                this.headerLog.debug(this.id + " >> " + header.toString());
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(this.id);
        buf.append(" [");
        switch (this.status) {
            case 0: {
                buf.append("ACTIVE");
                if (!this.inbuf.hasData()) break;
                buf.append("(").append(this.inbuf.length()).append(")");
                break;
            }
            case 1: {
                buf.append("CLOSING");
                break;
            }
            case 2: {
                buf.append("CLOSED");
            }
        }
        buf.append("]");
        return buf.toString();
    }
}

