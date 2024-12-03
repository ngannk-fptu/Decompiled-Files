/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.repository.query;

import java.lang.reflect.Method;
import org.springframework.data.repository.core.RepositoryCreationException;
import org.springframework.data.repository.query.QueryMethod;

public final class QueryCreationException
extends RepositoryCreationException {
    private static final long serialVersionUID = -1238456123580L;
    private static final String MESSAGE_TEMPLATE = "Could not create query for method %s! Could not find property %s on domain class %s.";
    private final Method method;

    private QueryCreationException(String message, QueryMethod method) {
        super(message, method.getMetadata().getRepositoryInterface());
        this.method = method.getMethod();
    }

    private QueryCreationException(String message, Throwable cause, Class<?> repositoryInterface, Method method) {
        super(message, cause, repositoryInterface);
        this.method = method;
    }

    public static QueryCreationException invalidProperty(QueryMethod method, String propertyName) {
        return new QueryCreationException(String.format(MESSAGE_TEMPLATE, method, propertyName, method.getDomainClass().getName()), method);
    }

    public static QueryCreationException create(QueryMethod method, String message) {
        return new QueryCreationException(String.format("Could not create query for %s! Reason: %s", method, message), method);
    }

    public static QueryCreationException create(QueryMethod method, Throwable cause) {
        return new QueryCreationException(cause.getMessage(), cause, method.getMetadata().getRepositoryInterface(), method.getMethod());
    }

    public static QueryCreationException create(String message, Throwable cause, Class<?> repositoryInterface, Method method) {
        return new QueryCreationException(String.format("Could not create query for %s! Reason: %s", method, message), cause, repositoryInterface, method);
    }

    public Method getMethod() {
        return this.method;
    }
}

