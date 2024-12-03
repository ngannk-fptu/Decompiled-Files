/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.client5.http.cookie.Cookie
 *  org.apache.hc.client5.http.protocol.HttpClientContext
 *  org.apache.hc.core5.http.HttpMessage
 *  org.apache.hc.core5.http.HttpResponse
 *  org.apache.hc.core5.http.Message
 *  org.reactivestreams.Publisher
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.core.io.buffer.DataBufferFactory
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  reactor.core.publisher.Flux
 */
package org.springframework.http.client.reactive;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpMessage;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.Message;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.client.reactive.HttpComponentsHeadersAdapter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

class HttpComponentsClientHttpResponse
implements ClientHttpResponse {
    private final DataBufferFactory dataBufferFactory;
    private final Message<HttpResponse, Publisher<ByteBuffer>> message;
    private final HttpHeaders headers;
    private final HttpClientContext context;
    private final AtomicBoolean rejectSubscribers = new AtomicBoolean();

    public HttpComponentsClientHttpResponse(DataBufferFactory dataBufferFactory, Message<HttpResponse, Publisher<ByteBuffer>> message, HttpClientContext context) {
        this.dataBufferFactory = dataBufferFactory;
        this.message = message;
        this.context = context;
        HttpComponentsHeadersAdapter adapter = new HttpComponentsHeadersAdapter((HttpMessage)message.getHead());
        this.headers = HttpHeaders.readOnlyHttpHeaders(adapter);
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.valueOf(((HttpResponse)this.message.getHead()).getCode());
    }

    @Override
    public int getRawStatusCode() {
        return ((HttpResponse)this.message.getHead()).getCode();
    }

    @Override
    public MultiValueMap<String, ResponseCookie> getCookies() {
        LinkedMultiValueMap result = new LinkedMultiValueMap();
        this.context.getCookieStore().getCookies().forEach(cookie -> result.add((Object)cookie.getName(), (Object)ResponseCookie.fromClientResponse(cookie.getName(), cookie.getValue()).domain(cookie.getDomain()).path(cookie.getPath()).maxAge(this.getMaxAgeSeconds((Cookie)cookie)).secure(cookie.isSecure()).httpOnly(cookie.containsAttribute("httponly")).sameSite(cookie.getAttribute("samesite")).build()));
        return result;
    }

    private long getMaxAgeSeconds(Cookie cookie) {
        String maxAgeAttribute = cookie.getAttribute("max-age");
        return maxAgeAttribute != null ? Long.parseLong(maxAgeAttribute) : -1L;
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return Flux.from((Publisher)((Publisher)this.message.getBody())).doOnSubscribe(s -> {
            if (!this.rejectSubscribers.compareAndSet(false, true)) {
                throw new IllegalStateException("The client response body can only be consumed once.");
            }
        }).map(arg_0 -> ((DataBufferFactory)this.dataBufferFactory).wrap(arg_0));
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.headers;
    }
}

