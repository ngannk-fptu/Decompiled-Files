/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.EntityDetails
 *  org.apache.hc.core5.http.Header
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.HttpResponse
 *  org.apache.hc.core5.http.message.RequestLine
 *  org.apache.hc.core5.http.message.StatusLine
 *  org.apache.hc.core5.http.nio.AsyncClientExchangeHandler
 *  org.apache.hc.core5.http.nio.CapacityChannel
 *  org.apache.hc.core5.http.nio.DataStreamChannel
 *  org.apache.hc.core5.http.nio.RequestChannel
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.util.Identifiable
 *  org.slf4j.Logger
 */
package org.apache.hc.client5.http.impl.async;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.message.RequestLine;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.hc.core5.http.nio.AsyncClientExchangeHandler;
import org.apache.hc.core5.http.nio.CapacityChannel;
import org.apache.hc.core5.http.nio.DataStreamChannel;
import org.apache.hc.core5.http.nio.RequestChannel;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Identifiable;
import org.slf4j.Logger;

final class LoggingAsyncClientExchangeHandler
implements AsyncClientExchangeHandler,
Identifiable {
    private final Logger log;
    private final String exchangeId;
    private final AsyncClientExchangeHandler handler;

    LoggingAsyncClientExchangeHandler(Logger log, String exchangeId, AsyncClientExchangeHandler handler) {
        this.log = log;
        this.exchangeId = exchangeId;
        this.handler = handler;
    }

    public String getId() {
        return this.exchangeId;
    }

    public void releaseResources() {
        this.handler.releaseResources();
    }

    public void produceRequest(RequestChannel channel, HttpContext context) throws HttpException, IOException {
        this.handler.produceRequest((request, entityDetails, context1) -> {
            if (this.log.isDebugEnabled()) {
                this.log.debug("{} send request {}, {}", new Object[]{this.exchangeId, new RequestLine(request), entityDetails != null ? "entity len " + entityDetails.getContentLength() : "null entity"});
            }
            channel.sendRequest(request, entityDetails, context1);
        }, context);
    }

    public int available() {
        return this.handler.available();
    }

    public void produce(final DataStreamChannel channel) throws IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("{}: produce request data", (Object)this.exchangeId);
        }
        this.handler.produce(new DataStreamChannel(){

            public void requestOutput() {
                channel.requestOutput();
            }

            public int write(ByteBuffer src) throws IOException {
                if (LoggingAsyncClientExchangeHandler.this.log.isDebugEnabled()) {
                    LoggingAsyncClientExchangeHandler.this.log.debug("{}: produce request data, len {} bytes", (Object)LoggingAsyncClientExchangeHandler.this.exchangeId, (Object)src.remaining());
                }
                return channel.write(src);
            }

            public void endStream() throws IOException {
                if (LoggingAsyncClientExchangeHandler.this.log.isDebugEnabled()) {
                    LoggingAsyncClientExchangeHandler.this.log.debug("{}: end of request data", (Object)LoggingAsyncClientExchangeHandler.this.exchangeId);
                }
                channel.endStream();
            }

            public void endStream(List<? extends Header> trailers) throws IOException {
                if (LoggingAsyncClientExchangeHandler.this.log.isDebugEnabled()) {
                    LoggingAsyncClientExchangeHandler.this.log.debug("{}: end of request data", (Object)LoggingAsyncClientExchangeHandler.this.exchangeId);
                }
                channel.endStream(trailers);
            }
        });
    }

    public void consumeInformation(HttpResponse response, HttpContext context) throws HttpException, IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("{}: information response {}", (Object)this.exchangeId, (Object)new StatusLine(response));
        }
        this.handler.consumeInformation(response, context);
    }

    public void consumeResponse(HttpResponse response, EntityDetails entityDetails, HttpContext context) throws HttpException, IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("{}: consume response {}, {}", new Object[]{this.exchangeId, new StatusLine(response), entityDetails != null ? "entity len " + entityDetails.getContentLength() : " null entity"});
        }
        this.handler.consumeResponse(response, entityDetails, context);
    }

    public void updateCapacity(CapacityChannel capacityChannel) throws IOException {
        this.handler.updateCapacity(increment -> {
            if (this.log.isDebugEnabled()) {
                this.log.debug("{} capacity update {}", (Object)this.exchangeId, (Object)increment);
            }
            capacityChannel.update(increment);
        });
    }

    public void consume(ByteBuffer src) throws IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("{}: consume response data, len {} bytes", (Object)this.exchangeId, (Object)src.remaining());
        }
        this.handler.consume(src);
    }

    public void streamEnd(List<? extends Header> trailers) throws HttpException, IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("{}: end of response data", (Object)this.exchangeId);
        }
        this.handler.streamEnd(trailers);
    }

    public void failed(Exception cause) {
        if (this.log.isDebugEnabled()) {
            this.log.debug("{}: execution failed: {}", (Object)this.exchangeId, (Object)cause.getMessage());
        }
        this.handler.failed(cause);
    }

    public void cancel() {
        if (this.log.isDebugEnabled()) {
            this.log.debug("{}: execution cancelled", (Object)this.exchangeId);
        }
        this.handler.cancel();
    }
}

