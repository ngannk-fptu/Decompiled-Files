/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.http.Header
 *  org.apache.http.HttpRequest
 *  org.apache.http.HttpResponse
 *  org.apache.http.config.MessageConstraints
 *  org.apache.http.entity.ContentLengthStrategy
 *  org.apache.http.io.HttpMessageParserFactory
 *  org.apache.http.io.HttpMessageWriterFactory
 */
package org.apache.http.impl.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import org.apache.commons.logging.Log;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.config.MessageConstraints;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.conn.DefaultManagedHttpClientConnection;
import org.apache.http.impl.conn.LoggingInputStream;
import org.apache.http.impl.conn.LoggingOutputStream;
import org.apache.http.impl.conn.Wire;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;

class LoggingManagedHttpClientConnection
extends DefaultManagedHttpClientConnection {
    private final Log log;
    private final Log headerLog;
    private final Wire wire;

    public LoggingManagedHttpClientConnection(String id, Log log, Log headerLog, Log wireLog, int bufferSize, int fragmentSizeHint, CharsetDecoder charDecoder, CharsetEncoder charEncoder, MessageConstraints constraints, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, HttpMessageWriterFactory<HttpRequest> requestWriterFactory, HttpMessageParserFactory<HttpResponse> responseParserFactory) {
        super(id, bufferSize, fragmentSizeHint, charDecoder, charEncoder, constraints, incomingContentStrategy, outgoingContentStrategy, requestWriterFactory, responseParserFactory);
        this.log = log;
        this.headerLog = headerLog;
        this.wire = new Wire(wireLog, id);
    }

    public void close() throws IOException {
        if (super.isOpen()) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)(this.getId() + ": Close connection"));
            }
            super.close();
        }
    }

    public void setSocketTimeout(int timeout) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)(this.getId() + ": set socket timeout to " + timeout));
        }
        super.setSocketTimeout(timeout);
    }

    @Override
    public void shutdown() throws IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)(this.getId() + ": Shutdown connection"));
        }
        super.shutdown();
    }

    protected InputStream getSocketInputStream(Socket socket) throws IOException {
        InputStream in = super.getSocketInputStream(socket);
        if (this.wire.enabled()) {
            in = new LoggingInputStream(in, this.wire);
        }
        return in;
    }

    protected OutputStream getSocketOutputStream(Socket socket) throws IOException {
        OutputStream out = super.getSocketOutputStream(socket);
        if (this.wire.enabled()) {
            out = new LoggingOutputStream(out, this.wire);
        }
        return out;
    }

    protected void onResponseReceived(HttpResponse response) {
        if (response != null && this.headerLog.isDebugEnabled()) {
            Header[] headers;
            this.headerLog.debug((Object)(this.getId() + " << " + response.getStatusLine().toString()));
            for (Header header : headers = response.getAllHeaders()) {
                this.headerLog.debug((Object)(this.getId() + " << " + header.toString()));
            }
        }
    }

    protected void onRequestSubmitted(HttpRequest request) {
        if (request != null && this.headerLog.isDebugEnabled()) {
            Header[] headers;
            this.headerLog.debug((Object)(this.getId() + " >> " + request.getRequestLine().toString()));
            for (Header header : headers = request.getAllHeaders()) {
                this.headerLog.debug((Object)(this.getId() + " >> " + header.toString()));
            }
        }
    }
}

