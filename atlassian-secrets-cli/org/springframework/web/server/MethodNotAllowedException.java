/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.server.ResponseStatusException;

public class MethodNotAllowedException
extends ResponseStatusException {
    private final String method;
    private final Set<HttpMethod> supportedMethods;

    public MethodNotAllowedException(HttpMethod method, Collection<HttpMethod> supportedMethods) {
        this(method.name(), supportedMethods);
    }

    public MethodNotAllowedException(String method, @Nullable Collection<HttpMethod> supportedMethods) {
        super(HttpStatus.METHOD_NOT_ALLOWED, "Request method '" + method + "' not supported");
        Assert.notNull((Object)method, "'method' is required");
        if (supportedMethods == null) {
            supportedMethods = Collections.emptySet();
        }
        this.method = method;
        this.supportedMethods = Collections.unmodifiableSet(new HashSet<HttpMethod>(supportedMethods));
    }

    public String getHttpMethod() {
        return this.method;
    }

    public Set<HttpMethod> getSupportedMethods() {
        return this.supportedMethods;
    }
}

