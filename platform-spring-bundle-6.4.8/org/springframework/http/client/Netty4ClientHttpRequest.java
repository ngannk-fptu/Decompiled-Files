/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.bootstrap.Bootstrap
 *  io.netty.buffer.ByteBufOutputStream
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.SimpleChannelInboundHandler
 *  io.netty.handler.codec.http.DefaultFullHttpRequest
 *  io.netty.handler.codec.http.FullHttpRequest
 *  io.netty.handler.codec.http.FullHttpResponse
 *  io.netty.handler.codec.http.HttpMethod
 *  io.netty.handler.codec.http.HttpVersion
 *  io.netty.util.concurrent.GenericFutureListener
 */
package org.springframework.http.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AbstractAsyncClientHttpRequest;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.Netty4ClientHttpResponse;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

@Deprecated
class Netty4ClientHttpRequest
extends AbstractAsyncClientHttpRequest
implements ClientHttpRequest {
    private final Bootstrap bootstrap;
    private final URI uri;
    private final HttpMethod method;
    private final ByteBufOutputStream body;

    public Netty4ClientHttpRequest(Bootstrap bootstrap, URI uri, HttpMethod method) {
        this.bootstrap = bootstrap;
        this.uri = uri;
        this.method = method;
        this.body = new ByteBufOutputStream(Unpooled.buffer((int)1024));
    }

    @Override
    public HttpMethod getMethod() {
        return this.method;
    }

    @Override
    public String getMethodValue() {
        return this.method.name();
    }

    @Override
    public URI getURI() {
        return this.uri;
    }

    @Override
    public ClientHttpResponse execute() throws IOException {
        try {
            return (ClientHttpResponse)this.executeAsync().get();
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted during request execution", ex);
        }
        catch (ExecutionException ex) {
            if (ex.getCause() instanceof IOException) {
                throw (IOException)ex.getCause();
            }
            throw new IOException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    protected OutputStream getBodyInternal(HttpHeaders headers) throws IOException {
        return this.body;
    }

    @Override
    protected ListenableFuture<ClientHttpResponse> executeInternal(HttpHeaders headers) throws IOException {
        SettableListenableFuture<ClientHttpResponse> responseFuture = new SettableListenableFuture<ClientHttpResponse>();
        ChannelFutureListener connectionListener = future -> {
            if (future.isSuccess()) {
                Channel channel = future.channel();
                channel.pipeline().addLast(new ChannelHandler[]{new RequestExecuteHandler(responseFuture)});
                FullHttpRequest nettyRequest = this.createFullHttpRequest(headers);
                channel.writeAndFlush((Object)nettyRequest);
            } else {
                responseFuture.setException(future.cause());
            }
        };
        this.bootstrap.connect(this.uri.getHost(), Netty4ClientHttpRequest.getPort(this.uri)).addListener((GenericFutureListener)connectionListener);
        return responseFuture;
    }

    private FullHttpRequest createFullHttpRequest(HttpHeaders headers) {
        io.netty.handler.codec.http.HttpMethod nettyMethod = io.netty.handler.codec.http.HttpMethod.valueOf((String)this.method.name());
        String authority = this.uri.getRawAuthority();
        String path = this.uri.toString().substring(this.uri.toString().indexOf(authority) + authority.length());
        DefaultFullHttpRequest nettyRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, nettyMethod, path, this.body.buffer());
        nettyRequest.headers().set("Host", (Object)(this.uri.getHost() + ":" + Netty4ClientHttpRequest.getPort(this.uri)));
        nettyRequest.headers().set("Connection", (Object)"close");
        headers.forEach((arg_0, arg_1) -> Netty4ClientHttpRequest.lambda$createFullHttpRequest$1((FullHttpRequest)nettyRequest, arg_0, arg_1));
        if (!nettyRequest.headers().contains("Content-Length") && this.body.buffer().readableBytes() > 0) {
            nettyRequest.headers().set("Content-Length", (Object)this.body.buffer().readableBytes());
        }
        return nettyRequest;
    }

    private static int getPort(URI uri) {
        int port = uri.getPort();
        if (port == -1) {
            if ("http".equalsIgnoreCase(uri.getScheme())) {
                port = 80;
            } else if ("https".equalsIgnoreCase(uri.getScheme())) {
                port = 443;
            }
        }
        return port;
    }

    private static /* synthetic */ void lambda$createFullHttpRequest$1(FullHttpRequest nettyRequest, String headerName, List headerValues) {
        nettyRequest.headers().add(headerName, (Iterable)headerValues);
    }

    private static class RequestExecuteHandler
    extends SimpleChannelInboundHandler<FullHttpResponse> {
        private final SettableListenableFuture<ClientHttpResponse> responseFuture;

        public RequestExecuteHandler(SettableListenableFuture<ClientHttpResponse> responseFuture) {
            this.responseFuture = responseFuture;
        }

        protected void channelRead0(ChannelHandlerContext context, FullHttpResponse response) throws Exception {
            this.responseFuture.set(new Netty4ClientHttpResponse(context, response));
        }

        public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
            this.responseFuture.setException(cause);
        }
    }
}

