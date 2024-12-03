/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client.reactive;

import java.net.URI;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpMethod;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.util.MultiValueMap;

public interface ClientHttpRequest
extends ReactiveHttpOutputMessage {
    public HttpMethod getMethod();

    public URI getURI();

    public MultiValueMap<String, HttpCookie> getCookies();

    public <T> T getNativeRequest();
}

