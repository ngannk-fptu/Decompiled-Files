/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 */
package org.springframework.web.server;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

public class MethodNotAllowedException
extends ResponseStatusException {
    private final String method;
    private final Set<HttpMethod> httpMethods;

    public MethodNotAllowedException(HttpMethod method, Collection<HttpMethod> supportedMethods) {
        this(method.name(), supportedMethods);
    }

    public MethodNotAllowedException(String method, @Nullable Collection<HttpMethod> supportedMethods) {
        super(HttpStatus.METHOD_NOT_ALLOWED, "Request method '" + method + "' not supported");
        Assert.notNull((Object)method, (String)"'method' is required");
        if (supportedMethods == null) {
            supportedMethods = Collections.emptySet();
        }
        this.method = method;
        this.httpMethods = Collections.unmodifiableSet(new LinkedHashSet<HttpMethod>(supportedMethods));
    }

    @Override
    public Map<String, String> getHeaders() {
        return this.getResponseHeaders().toSingleValueMap();
    }

    @Override
    public HttpHeaders getResponseHeaders() {
        if (CollectionUtils.isEmpty(this.httpMethods)) {
            return HttpHeaders.EMPTY;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setAllow(this.httpMethods);
        return headers;
    }

    public String getHttpMethod() {
        return this.method;
    }

    public Set<HttpMethod> getSupportedMethods() {
        return this.httpMethods;
    }
}

