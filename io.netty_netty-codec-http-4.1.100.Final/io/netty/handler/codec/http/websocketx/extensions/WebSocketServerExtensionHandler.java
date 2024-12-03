/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.ChannelDuplexHandler
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelPromise
 *  io.netty.util.concurrent.GenericFutureListener
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.handler.codec.http.websocketx.extensions;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionUtil;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandshaker;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

public class WebSocketServerExtensionHandler
extends ChannelDuplexHandler {
    private final List<WebSocketServerExtensionHandshaker> extensionHandshakers;
    private final Queue<List<WebSocketServerExtension>> validExtensions = new ArrayDeque<List<WebSocketServerExtension>>(4);

    public WebSocketServerExtensionHandler(WebSocketServerExtensionHandshaker ... extensionHandshakers) {
        this.extensionHandshakers = Arrays.asList(ObjectUtil.checkNonEmpty((Object[])extensionHandshakers, (String)"extensionHandshakers"));
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg != LastHttpContent.EMPTY_LAST_CONTENT) {
            if (msg instanceof DefaultHttpRequest) {
                this.onHttpRequestChannelRead(ctx, (DefaultHttpRequest)msg);
            } else if (msg instanceof HttpRequest) {
                this.onHttpRequestChannelRead(ctx, (HttpRequest)msg);
            } else {
                super.channelRead(ctx, msg);
            }
        } else {
            super.channelRead(ctx, msg);
        }
    }

    protected void onHttpRequestChannelRead(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
        String extensionsHeader;
        List validExtensionsList = null;
        if (WebSocketExtensionUtil.isWebsocketUpgrade(request.headers()) && (extensionsHeader = request.headers().getAsString((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS)) != null) {
            List<WebSocketExtensionData> extensions = WebSocketExtensionUtil.extractExtensions(extensionsHeader);
            int rsv = 0;
            for (WebSocketExtensionData extensionData : extensions) {
                Iterator<WebSocketServerExtensionHandshaker> extensionHandshakersIterator = this.extensionHandshakers.iterator();
                WebSocketExtension validExtension = null;
                while (validExtension == null && extensionHandshakersIterator.hasNext()) {
                    WebSocketServerExtensionHandshaker extensionHandshaker = extensionHandshakersIterator.next();
                    validExtension = extensionHandshaker.handshakeExtension(extensionData);
                }
                if (validExtension == null || (validExtension.rsv() & rsv) != 0) continue;
                if (validExtensionsList == null) {
                    validExtensionsList = new ArrayList(1);
                }
                rsv |= validExtension.rsv();
                validExtensionsList.add(validExtension);
            }
        }
        if (validExtensionsList == null) {
            validExtensionsList = Collections.emptyList();
        }
        this.validExtensions.offer(validExtensionsList);
        super.channelRead(ctx, (Object)request);
    }

    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg != Unpooled.EMPTY_BUFFER && !(msg instanceof ByteBuf)) {
            if (msg instanceof DefaultHttpResponse) {
                this.onHttpResponseWrite(ctx, (DefaultHttpResponse)msg, promise);
            } else if (msg instanceof HttpResponse) {
                this.onHttpResponseWrite(ctx, (HttpResponse)msg, promise);
            } else {
                super.write(ctx, msg, promise);
            }
        } else {
            super.write(ctx, msg, promise);
        }
    }

    protected void onHttpResponseWrite(ChannelHandlerContext ctx, HttpResponse response, ChannelPromise promise) throws Exception {
        List<WebSocketServerExtension> validExtensionsList = this.validExtensions.poll();
        HttpResponse httpResponse = response;
        if (HttpResponseStatus.SWITCHING_PROTOCOLS.equals(httpResponse.status())) {
            this.handlePotentialUpgrade(ctx, promise, httpResponse, validExtensionsList);
        }
        super.write(ctx, (Object)response, promise);
    }

    private void handlePotentialUpgrade(final ChannelHandlerContext ctx, ChannelPromise promise, HttpResponse httpResponse, final List<WebSocketServerExtension> validExtensionsList) {
        HttpHeaders headers = httpResponse.headers();
        if (WebSocketExtensionUtil.isWebsocketUpgrade(headers)) {
            if (validExtensionsList != null && !validExtensionsList.isEmpty()) {
                String headerValue = headers.getAsString((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS);
                ArrayList<WebSocketExtensionData> extraExtensions = new ArrayList<WebSocketExtensionData>(this.extensionHandshakers.size());
                for (WebSocketServerExtension extension : validExtensionsList) {
                    extraExtensions.add(extension.newReponseData());
                }
                String newHeaderValue = WebSocketExtensionUtil.computeMergeExtensionsHeaderValue(headerValue, extraExtensions);
                promise.addListener((GenericFutureListener)new ChannelFutureListener(){

                    public void operationComplete(ChannelFuture future) {
                        if (future.isSuccess()) {
                            for (WebSocketServerExtension extension : validExtensionsList) {
                                WebSocketExtensionDecoder decoder = extension.newExtensionDecoder();
                                WebSocketExtensionEncoder encoder = extension.newExtensionEncoder();
                                String name = ctx.name();
                                ctx.pipeline().addAfter(name, ((Object)((Object)decoder)).getClass().getName(), (ChannelHandler)decoder).addAfter(name, ((Object)((Object)encoder)).getClass().getName(), (ChannelHandler)encoder);
                            }
                        }
                    }
                });
                if (newHeaderValue != null) {
                    headers.set((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS, (Object)newHeaderValue);
                }
            }
            promise.addListener((GenericFutureListener)new ChannelFutureListener(){

                public void operationComplete(ChannelFuture future) {
                    if (future.isSuccess()) {
                        ctx.pipeline().remove((ChannelHandler)WebSocketServerExtensionHandler.this);
                    }
                }
            });
        }
    }
}

