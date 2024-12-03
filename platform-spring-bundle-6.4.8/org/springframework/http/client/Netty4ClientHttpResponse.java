/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBufInputStream
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.handler.codec.http.FullHttpResponse
 */
package org.springframework.http.client;

import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.AbstractClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Deprecated
class Netty4ClientHttpResponse
extends AbstractClientHttpResponse {
    private final ChannelHandlerContext context;
    private final FullHttpResponse nettyResponse;
    private final ByteBufInputStream body;
    @Nullable
    private volatile HttpHeaders headers;

    public Netty4ClientHttpResponse(ChannelHandlerContext context, FullHttpResponse nettyResponse) {
        Assert.notNull((Object)context, "ChannelHandlerContext must not be null");
        Assert.notNull((Object)nettyResponse, "FullHttpResponse must not be null");
        this.context = context;
        this.nettyResponse = nettyResponse;
        this.body = new ByteBufInputStream(this.nettyResponse.content());
        this.nettyResponse.retain();
    }

    @Override
    public int getRawStatusCode() throws IOException {
        return this.nettyResponse.getStatus().code();
    }

    @Override
    public String getStatusText() throws IOException {
        return this.nettyResponse.getStatus().reasonPhrase();
    }

    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders headers = this.headers;
        if (headers == null) {
            headers = new HttpHeaders();
            for (Map.Entry entry : this.nettyResponse.headers()) {
                headers.add((String)entry.getKey(), (String)entry.getValue());
            }
            this.headers = headers;
        }
        return headers;
    }

    @Override
    public InputStream getBody() throws IOException {
        return this.body;
    }

    @Override
    public void close() {
        this.nettyResponse.release();
        this.context.close();
    }
}

