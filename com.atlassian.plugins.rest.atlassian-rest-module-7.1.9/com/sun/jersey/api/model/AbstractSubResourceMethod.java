/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.model;

import com.sun.jersey.api.model.AbstractModelVisitor;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.PathAnnotated;
import com.sun.jersey.api.model.PathValue;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class AbstractSubResourceMethod
extends AbstractResourceMethod
implements PathAnnotated {
    private PathValue uriPath;

    public AbstractSubResourceMethod(AbstractResource resource, Method method, Class returnType, Type genericReturnType, PathValue uriPath, String httpMethod, Annotation[] annotations) {
        super(resource, method, returnType, genericReturnType, httpMethod, annotations);
        this.uriPath = uriPath;
    }

    @Override
    public PathValue getPath() {
        return this.uriPath;
    }

    @Override
    public void accept(AbstractModelVisitor visitor) {
        visitor.visitAbstractSubResourceMethod(this);
    }

    @Override
    public String toString() {
        return "AbstractSubResourceMethod(" + this.getMethod().getDeclaringClass().getSimpleName() + "#" + this.getMethod().getName() + ")";
    }
}

