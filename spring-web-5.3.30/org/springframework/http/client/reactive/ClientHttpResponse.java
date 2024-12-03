/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.http.client.reactive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.ResponseCookie;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

public interface ClientHttpResponse
extends ReactiveHttpInputMessage {
    default public String getId() {
        return ObjectUtils.getIdentityHexString((Object)this);
    }

    public HttpStatus getStatusCode();

    public int getRawStatusCode();

    public MultiValueMap<String, ResponseCookie> getCookies();
}

