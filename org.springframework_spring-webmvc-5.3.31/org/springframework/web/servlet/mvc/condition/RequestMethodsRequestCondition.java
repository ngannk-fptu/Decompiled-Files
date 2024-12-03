/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.DispatcherType
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.http.HttpMethod
 *  org.springframework.lang.Nullable
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.web.bind.annotation.RequestMethod
 *  org.springframework.web.cors.CorsUtils
 */
package org.springframework.web.servlet.mvc.condition;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;

public final class RequestMethodsRequestCondition
extends AbstractRequestCondition<RequestMethodsRequestCondition> {
    private static final Map<String, RequestMethodsRequestCondition> requestMethodConditionCache = CollectionUtils.newHashMap((int)RequestMethod.values().length);
    private final Set<RequestMethod> methods;

    public RequestMethodsRequestCondition(RequestMethod ... requestMethods) {
        this.methods = ObjectUtils.isEmpty((Object[])requestMethods) ? Collections.emptySet() : new LinkedHashSet<RequestMethod>(Arrays.asList(requestMethods));
    }

    private RequestMethodsRequestCondition(Set<RequestMethod> methods) {
        this.methods = methods;
    }

    public Set<RequestMethod> getMethods() {
        return this.methods;
    }

    @Override
    protected Collection<RequestMethod> getContent() {
        return this.methods;
    }

    @Override
    protected String getToStringInfix() {
        return " || ";
    }

    @Override
    public RequestMethodsRequestCondition combine(RequestMethodsRequestCondition other) {
        if (this.isEmpty() && other.isEmpty()) {
            return this;
        }
        if (other.isEmpty()) {
            return this;
        }
        if (this.isEmpty()) {
            return other;
        }
        LinkedHashSet<RequestMethod> set = new LinkedHashSet<RequestMethod>(this.methods);
        set.addAll(other.methods);
        return new RequestMethodsRequestCondition(set);
    }

    @Override
    @Nullable
    public RequestMethodsRequestCondition getMatchingCondition(HttpServletRequest request) {
        if (CorsUtils.isPreFlightRequest((HttpServletRequest)request)) {
            return this.matchPreFlight(request);
        }
        if (this.getMethods().isEmpty()) {
            if (RequestMethod.OPTIONS.name().equals(request.getMethod()) && !DispatcherType.ERROR.equals((Object)request.getDispatcherType())) {
                return null;
            }
            return this;
        }
        return this.matchRequestMethod(request.getMethod());
    }

    @Nullable
    private RequestMethodsRequestCondition matchPreFlight(HttpServletRequest request) {
        if (this.getMethods().isEmpty()) {
            return this;
        }
        String expectedMethod = request.getHeader("Access-Control-Request-Method");
        return this.matchRequestMethod(expectedMethod);
    }

    @Nullable
    private RequestMethodsRequestCondition matchRequestMethod(String httpMethodValue) {
        try {
            RequestMethod requestMethod = RequestMethod.valueOf((String)httpMethodValue);
            if (this.getMethods().contains(requestMethod)) {
                return requestMethodConditionCache.get(httpMethodValue);
            }
            if (requestMethod.equals((Object)RequestMethod.HEAD) && this.getMethods().contains(RequestMethod.GET)) {
                return requestMethodConditionCache.get(HttpMethod.GET.name());
            }
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        return null;
    }

    @Override
    public int compareTo(RequestMethodsRequestCondition other, HttpServletRequest request) {
        if (other.methods.size() != this.methods.size()) {
            return other.methods.size() - this.methods.size();
        }
        if (this.methods.size() == 1) {
            if (this.methods.contains(RequestMethod.HEAD) && other.methods.contains(RequestMethod.GET)) {
                return -1;
            }
            if (this.methods.contains(RequestMethod.GET) && other.methods.contains(RequestMethod.HEAD)) {
                return 1;
            }
        }
        return 0;
    }

    static {
        for (RequestMethod method : RequestMethod.values()) {
            requestMethodConditionCache.put(method.name(), new RequestMethodsRequestCondition(method));
        }
    }
}

