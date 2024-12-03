/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpFields
 *  org.eclipse.jetty.http.HttpHeader
 *  org.eclipse.jetty.http.HttpHeaderValue
 *  org.eclipse.jetty.http.HttpVersion
 *  org.eclipse.jetty.http.MetaData
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client.http;

import java.util.concurrent.atomic.LongAdder;
import org.eclipse.jetty.client.HttpChannel;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.http.HttpConnectionOverHTTP;
import org.eclipse.jetty.client.http.HttpReceiverOverHTTP;
import org.eclipse.jetty.client.http.HttpSenderOverHTTP;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpHeaderValue;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.MetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpChannelOverHTTP
extends HttpChannel {
    private static final Logger LOG = LoggerFactory.getLogger(HttpChannelOverHTTP.class);
    private final HttpConnectionOverHTTP connection;
    private final HttpSenderOverHTTP sender;
    private final HttpReceiverOverHTTP receiver;
    private final LongAdder outMessages = new LongAdder();

    public HttpChannelOverHTTP(HttpConnectionOverHTTP connection) {
        super(connection.getHttpDestination());
        this.connection = connection;
        this.sender = this.newHttpSender();
        this.receiver = this.newHttpReceiver();
    }

    protected HttpSenderOverHTTP newHttpSender() {
        return new HttpSenderOverHTTP(this);
    }

    protected HttpReceiverOverHTTP newHttpReceiver() {
        return new HttpReceiverOverHTTP(this);
    }

    @Override
    protected HttpSenderOverHTTP getHttpSender() {
        return this.sender;
    }

    @Override
    protected HttpReceiverOverHTTP getHttpReceiver() {
        return this.receiver;
    }

    public HttpConnectionOverHTTP getHttpConnection() {
        return this.connection;
    }

    @Override
    public void send(HttpExchange exchange) {
        this.outMessages.increment();
        this.sender.send(exchange);
    }

    @Override
    public void release() {
        this.connection.release();
    }

    public void receive() {
        this.receiver.receive();
    }

    @Override
    public void exchangeTerminated(HttpExchange exchange, Result result) {
        super.exchangeTerminated(exchange, result);
        String method = exchange.getRequest().getMethod();
        Response response = result.getResponse();
        int status = response.getStatus();
        HttpFields responseHeaders = response.getHeaders();
        boolean isTunnel = this.isTunnel(method, status);
        String closeReason = null;
        if (result.isFailed()) {
            closeReason = "failure";
        } else if (this.receiver.isShutdown()) {
            closeReason = "server close";
        } else if (this.sender.isShutdown() && status != 101) {
            closeReason = "client close";
        }
        if (closeReason == null) {
            if (response.getVersion().compareTo((Enum)HttpVersion.HTTP_1_1) < 0) {
                boolean keepAlive = responseHeaders.contains(HttpHeader.CONNECTION, HttpHeaderValue.KEEP_ALIVE.asString());
                if (!keepAlive && !isTunnel) {
                    closeReason = "http/1.0";
                }
            } else if (responseHeaders.contains(HttpHeader.CONNECTION, HttpHeaderValue.CLOSE.asString()) && !isTunnel) {
                closeReason = "http/1.1";
            }
        }
        if (closeReason != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Closing, reason: {} - {}", (Object)closeReason, (Object)this.connection);
            }
            if (result.isFailed()) {
                this.connection.close(result.getFailure());
            } else {
                this.connection.close();
            }
        } else if (status == 101 || isTunnel) {
            this.connection.remove();
        } else {
            this.release();
        }
    }

    protected long getMessagesIn() {
        return this.receiver.getMessagesIn();
    }

    protected long getMessagesOut() {
        return this.outMessages.longValue();
    }

    boolean isTunnel(String method, int status) {
        return MetaData.isTunnel((String)method, (int)status);
    }

    @Override
    public String toString() {
        return String.format("%s[send=%s,recv=%s]", super.toString(), this.sender, this.receiver);
    }
}

