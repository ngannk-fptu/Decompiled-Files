/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.rest.doclet.generators.resourcedoc;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.plugins.rest.doclet.generators.schema.RichClass;
import com.atlassian.rest.annotation.RequestType;
import com.atlassian.rest.annotation.ResponseType;
import com.atlassian.rest.annotation.ResponseTypes;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestMethod {
    private static final Logger log = LoggerFactory.getLogger(RestMethod.class);
    private final Class<?> resourceClass;
    private final Method method;

    private RestMethod(Class<?> resourceClass, Method method) {
        this.resourceClass = resourceClass;
        this.method = method;
    }

    public boolean isExperimental() {
        return this.method.getAnnotation(ExperimentalApi.class) != null;
    }

    public boolean isDeprecated() {
        return this.method.getAnnotation(Deprecated.class) != null;
    }

    public static RestMethod restMethod(Class<?> resourceClass, Method method) {
        return new RestMethod(resourceClass, method);
    }

    public Optional<RichClass> getRequestType() {
        Optional<RichClass> typeFromAnnotation = Optional.empty();
        Optional<Object> typeFromParameter = Optional.empty();
        if (this.method.isAnnotationPresent(RequestType.class)) {
            RequestType requestType = this.method.getAnnotation(RequestType.class);
            typeFromAnnotation = Optional.of(RichClass.of(requestType.value(), requestType.genericTypes()));
        }
        for (int i = 0; i < this.method.getParameterTypes().length; ++i) {
            if (this.method.getParameterAnnotations()[i].length != 0) continue;
            typeFromParameter = Optional.of(RichClass.of(this.method.getGenericParameterTypes()[i]));
            break;
        }
        if (typeFromAnnotation.isPresent() && !typeFromAnnotation.equals(typeFromParameter)) {
            log.warn(String.format("Method %s.%s declares request type that is different than the actual request parameter of this method. This may result in inaccurate documentation.", this.resourceClass.getSimpleName(), this.method.getName()));
        }
        return typeFromAnnotation.isPresent() ? typeFromAnnotation : typeFromParameter;
    }

    public List<RichClass> responseTypesFor(int status) {
        ArrayList<RichClass> types = new ArrayList<RichClass>();
        for (ResponseType responseType : this.declaredResponseTypes()) {
            if (status != responseType.status() && !this.matchesStatusType(status, responseType) || this.isVoidType(responseType)) continue;
            types.add(RichClass.of(responseType.value(), responseType.genericTypes()));
        }
        if (ResponseType.StatusType.SUCCESS.matches(status) && !this.method.getReturnType().equals(Response.class) && !"void".equalsIgnoreCase(this.method.getGenericReturnType().getTypeName())) {
            RichClass actualReturnType = RichClass.of(this.method.getGenericReturnType());
            if (!types.isEmpty() && !Collections.singletonList(actualReturnType).equals(types)) {
                log.warn(String.format("Method %s.%s declares response type for success response that is different than the actual return type of this method. This may result in inaccurate documentation.", this.resourceClass.getSimpleName(), this.method.getName()));
            } else {
                return Collections.singletonList(actualReturnType);
            }
        }
        return ImmutableList.copyOf(types);
    }

    private boolean matchesStatusType(int status, ResponseType responseType) {
        return responseType.status() == 0 && responseType.statusType().matches(status);
    }

    private boolean isVoidType(ResponseType responseType) {
        return ImmutableSet.of(Void.class, Void.TYPE).contains(responseType.value());
    }

    private Iterable<ResponseType> declaredResponseTypes() {
        ArrayList<ResponseType> responseTypes = new ArrayList<ResponseType>();
        responseTypes.addAll(this.responseTypes(this.method));
        responseTypes.addAll(this.responseTypes(this.resourceClass));
        return responseTypes;
    }

    private Collection<ResponseType> responseTypes(AnnotatedElement element) {
        if (element.isAnnotationPresent(ResponseType.class)) {
            return Collections.singleton(element.getAnnotation(ResponseType.class));
        }
        if (element.isAnnotationPresent(ResponseTypes.class)) {
            return Arrays.asList(element.getAnnotation(ResponseTypes.class).value());
        }
        return Collections.emptyList();
    }
}

