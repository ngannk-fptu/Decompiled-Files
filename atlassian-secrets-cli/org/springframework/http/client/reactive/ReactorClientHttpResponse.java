/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 *  reactor.ipc.netty.http.client.HttpClientResponse
 */
package org.springframework.http.client.reactive;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.ipc.netty.http.client.HttpClientResponse;

class ReactorClientHttpResponse
implements ClientHttpResponse {
    private final HttpClientResponse response;
    private final AtomicBoolean bodyConsumed = new AtomicBoolean();

    public ReactorClientHttpResponse(HttpClientResponse response) {
        this.response = response;
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return this.response.receive().doOnSubscribe(s -> Assert.state(this.bodyConsumed.compareAndSet(false, true), "The client response body can only be consumed once.")).map(byteBuf -> {
            byte[] data = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(data);
            return ReactorClientHttpConnector.BUFFER_FACTORY.wrap(data);
        });
    }

    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        this.response.responseHeaders().entries().forEach(e -> headers.add((String)e.getKey(), (String)e.getValue()));
        return headers;
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.valueOf(this.getRawStatusCode());
    }

    @Override
    public int getRawStatusCode() {
        return this.response.status().code();
    }

    @Override
    public MultiValueMap<String, ResponseCookie> getCookies() {
        LinkedMultiValueMap result = new LinkedMultiValueMap();
        this.response.cookies().values().stream().flatMap(Collection::stream).forEach(cookie -> result.add(cookie.name(), ResponseCookie.from(cookie.name(), cookie.value()).domain(cookie.domain()).path(cookie.path()).maxAge(cookie.maxAge()).secure(cookie.isSecure()).httpOnly(cookie.isHttpOnly()).build()));
        return CollectionUtils.unmodifiableMultiValueMap(result);
    }

    public String toString() {
        return "ReactorClientHttpResponse{request=[" + this.response.method().name() + " " + this.response.uri() + "],status=" + this.getRawStatusCode() + '}';
    }
}

