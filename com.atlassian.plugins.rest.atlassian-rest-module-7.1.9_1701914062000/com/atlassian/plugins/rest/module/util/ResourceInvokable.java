/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.module.util;

import com.sun.jersey.core.reflection.AnnotatedMethod;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.UriBuilder;
import net.sf.cglib.proxy.InvocationHandler;

public abstract class ResourceInvokable
implements InvocationHandler {
    protected Class<?> resourceClass;
    private final URI baseUri;

    public ResourceInvokable(Class<?> resourceClass, URI baseUri) {
        this.resourceClass = resourceClass;
        this.baseUri = baseUri;
    }

    protected Map<String, Object> buildParamMap(AnnotatedMethod called, Object[] args) {
        HashMap<String, Object> rv = new HashMap<String, Object>();
        Annotation[][] allParameterAnnotations = called.getParameterAnnotations();
        for (int i = 0; i < allParameterAnnotations.length; ++i) {
            Annotation[] parameterAnnotations;
            for (Annotation annotation : parameterAnnotations = allParameterAnnotations[i]) {
                if (!(annotation instanceof PathParam) || args[i] == null) continue;
                rv.put(((PathParam)annotation).value(), args[i]);
            }
        }
        return rv;
    }

    protected URI getURI(Method method, Object[] args) {
        UriBuilder builder = UriBuilder.fromUri(this.baseUri).path(this.resourceClass);
        if (new AnnotatedMethod(method).getAnnotation(Path.class) != null) {
            builder.path(method);
        }
        return builder.buildFromMap(this.buildParamMap(new AnnotatedMethod(method), args));
    }
}

