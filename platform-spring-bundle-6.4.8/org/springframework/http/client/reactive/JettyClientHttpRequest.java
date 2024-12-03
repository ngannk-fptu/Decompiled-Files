/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.client.api.Request
 *  org.eclipse.jetty.reactive.client.ContentChunk
 *  org.eclipse.jetty.reactive.client.ReactiveRequest
 *  org.eclipse.jetty.reactive.client.ReactiveRequest$Builder
 *  org.eclipse.jetty.reactive.client.ReactiveRequest$Content
 *  org.eclipse.jetty.util.Callback
 *  org.reactivestreams.Publisher
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 *  reactor.core.publisher.MonoSink
 */
package org.springframework.http.client.reactive;

import java.net.HttpCookie;
import java.net.URI;
import java.util.Collection;
import java.util.function.Function;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.reactive.client.ContentChunk;
import org.eclipse.jetty.reactive.client.ReactiveRequest;
import org.eclipse.jetty.util.Callback;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.PooledDataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.AbstractClientHttpRequest;
import org.springframework.http.client.reactive.Jetty10HttpFieldsHelper;
import org.springframework.http.client.reactive.JettyHeadersAdapter;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

class JettyClientHttpRequest
extends AbstractClientHttpRequest {
    private final Request jettyRequest;
    private final DataBufferFactory bufferFactory;
    private final ReactiveRequest.Builder builder;

    public JettyClientHttpRequest(Request jettyRequest, DataBufferFactory bufferFactory) {
        this.jettyRequest = jettyRequest;
        this.bufferFactory = bufferFactory;
        this.builder = ReactiveRequest.newBuilder((Request)this.jettyRequest).abortOnCancel(true);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.valueOf(this.jettyRequest.getMethod());
    }

    @Override
    public URI getURI() {
        return this.jettyRequest.getURI();
    }

    @Override
    public Mono<Void> setComplete() {
        return this.doCommit();
    }

    @Override
    public DataBufferFactory bufferFactory() {
        return this.bufferFactory;
    }

    @Override
    public <T> T getNativeRequest() {
        return (T)this.jettyRequest;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body2) {
        return Mono.create(sink -> {
            ReactiveRequest.Content content = (ReactiveRequest.Content)Flux.from((Publisher)body2).map(buffer -> this.toContentChunk((DataBuffer)buffer, (MonoSink<Void>)sink)).as(chunks -> ReactiveRequest.Content.fromPublisher((Publisher)chunks, (String)this.getContentType()));
            this.builder.content(content);
            sink.success();
        }).then(this.doCommit());
    }

    @Override
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body2) {
        return this.writeWith((Publisher<? extends DataBuffer>)Flux.from(body2).flatMap(Function.identity()).doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release));
    }

    private String getContentType() {
        MediaType contentType = this.getHeaders().getContentType();
        return contentType != null ? contentType.toString() : "application/octet-stream";
    }

    private ContentChunk toContentChunk(final DataBuffer buffer, final MonoSink<Void> sink) {
        return new ContentChunk(buffer.asByteBuffer(), new Callback(){

            public void succeeded() {
                DataBufferUtils.release(buffer);
            }

            public void failed(Throwable t) {
                DataBufferUtils.release(buffer);
                sink.error(t);
            }
        });
    }

    @Override
    protected void applyCookies() {
        this.getCookies().values().stream().flatMap(Collection::stream).map(cookie -> new HttpCookie(cookie.getName(), cookie.getValue())).forEach(arg_0 -> ((Request)this.jettyRequest).cookie(arg_0));
    }

    @Override
    protected void applyHeaders() {
        HttpHeaders headers = this.getHeaders();
        headers.forEach((key, value) -> value.forEach(v -> this.jettyRequest.header(key, v)));
        if (!headers.containsKey("Accept")) {
            this.jettyRequest.header("Accept", "*/*");
        }
    }

    @Override
    protected HttpHeaders initReadOnlyHeaders() {
        MultiValueMap<String, String> headers = Jetty10HttpFieldsHelper.jetty10Present() ? Jetty10HttpFieldsHelper.getHttpHeaders(this.jettyRequest) : new JettyHeadersAdapter(this.jettyRequest.getHeaders());
        return HttpHeaders.readOnlyHttpHeaders(headers);
    }

    public ReactiveRequest toReactiveRequest() {
        return this.builder.build();
    }
}

