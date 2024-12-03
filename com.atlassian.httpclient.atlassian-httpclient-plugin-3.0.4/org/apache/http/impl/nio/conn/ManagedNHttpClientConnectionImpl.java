/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.conn;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import javax.net.ssl.SSLSession;
import org.apache.commons.logging.Log;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.config.MessageConstraints;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.nio.DefaultNHttpClientConnection;
import org.apache.http.impl.nio.conn.LoggingIOSession;
import org.apache.http.nio.NHttpMessageParserFactory;
import org.apache.http.nio.NHttpMessageWriterFactory;
import org.apache.http.nio.conn.ManagedNHttpClientConnection;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.nio.reactor.ssl.SSLIOSession;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

class ManagedNHttpClientConnectionImpl
extends DefaultNHttpClientConnection
implements ManagedNHttpClientConnection {
    private final Log headerLog;
    private final Log wireLog;
    private final Log log;
    private final String id;
    private IOSession original;

    public ManagedNHttpClientConnectionImpl(String id, Log log, Log headerLog, Log wireLog, IOSession ioSession, int bufferSize, int fragmentSizeHint, ByteBufferAllocator allocator, CharsetDecoder charDecoder, CharsetEncoder charEncoder, MessageConstraints constraints, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, NHttpMessageWriterFactory<HttpRequest> requestWriterFactory, NHttpMessageParserFactory<HttpResponse> responseParserFactory) {
        super(ioSession, bufferSize, fragmentSizeHint, allocator, charDecoder, charEncoder, constraints, incomingContentStrategy, outgoingContentStrategy, requestWriterFactory, responseParserFactory);
        this.id = id;
        this.log = log;
        this.headerLog = headerLog;
        this.wireLog = wireLog;
        this.original = ioSession;
        if (this.log.isDebugEnabled() || this.wireLog.isDebugEnabled()) {
            super.bind(new LoggingIOSession(ioSession, this.id, this.log, this.wireLog));
        }
    }

    @Override
    public void bind(IOSession ioSession) {
        Args.notNull(ioSession, "I/O session");
        Asserts.check(!ioSession.isClosed(), "I/O session is closed");
        this.status = 0;
        this.original = ioSession;
        if (this.log.isDebugEnabled() || this.wireLog.isDebugEnabled()) {
            this.log.debug(this.id + " Upgrade session " + ioSession);
            super.bind(new LoggingIOSession(ioSession, this.id, this.log, this.wireLog));
        } else {
            super.bind(ioSession);
        }
    }

    @Override
    public IOSession getIOSession() {
        return this.original;
    }

    @Override
    public SSLSession getSSLSession() {
        return this.original instanceof SSLIOSession ? ((SSLIOSession)this.original).getSSLSession() : null;
    }

    @Override
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

