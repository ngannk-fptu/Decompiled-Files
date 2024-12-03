/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.reactive.client.ReactiveResponse
 *  org.reactivestreams.Publisher
 *  reactor.core.publisher.Flux
 */
package org.springframework.http.client.reactive;

import java.net.HttpCookie;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jetty.reactive.client.ReactiveResponse;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.client.reactive.Jetty10HttpFieldsHelper;
import org.springframework.http.client.reactive.JettyHeadersAdapter;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

class JettyClientHttpResponse
implements ClientHttpResponse {
    private static final Pattern SAMESITE_PATTERN = Pattern.compile("(?i).*SameSite=(Strict|Lax|None).*");
    private final ReactiveResponse reactiveResponse;
    private final Flux<DataBuffer> content;
    private final HttpHeaders headers;

    public JettyClientHttpResponse(ReactiveResponse reactiveResponse, Publisher<DataBuffer> content) {
        this.reactiveResponse = reactiveResponse;
        this.content = Flux.from(content);
        MultiValueMap<String, String> headers = Jetty10HttpFieldsHelper.jetty10Present() ? Jetty10HttpFieldsHelper.getHttpHeaders(reactiveResponse.getResponse()) : new JettyHeadersAdapter(reactiveResponse.getHeaders());
        this.headers = HttpHeaders.readOnlyHttpHeaders(headers);
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.valueOf(this.getRawStatusCode());
    }

    @Override
    public int getRawStatusCode() {
        return this.reactiveResponse.getStatus();
    }

    @Override
    public MultiValueMap<String, ResponseCookie> getCookies() {
        LinkedMultiValueMap result = new LinkedMultiValueMap();
        Object cookieHeader = this.getHeaders().get("Set-Cookie");
        if (cookieHeader != null) {
            cookieHeader.forEach(header -> HttpCookie.parse(header).forEach(cookie -> result.add(cookie.getName(), ResponseCookie.fromClientResponse(cookie.getName(), cookie.getValue()).domain(cookie.getDomain()).path(cookie.getPath()).maxAge(cookie.getMaxAge()).secure(cookie.getSecure()).httpOnly(cookie.isHttpOnly()).sameSite(JettyClientHttpResponse.parseSameSite(header)).build())));
        }
        return CollectionUtils.unmodifiableMultiValueMap(result);
    }

    @Nullable
    private static String parseSameSite(String headerValue) {
        Matcher matcher = SAMESITE_PATTERN.matcher(headerValue);
        return matcher.matches() ? matcher.group(1) : null;
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return this.content;
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.headers;
    }
}

