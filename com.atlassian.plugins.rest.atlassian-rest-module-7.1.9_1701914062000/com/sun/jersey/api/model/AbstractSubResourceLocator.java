/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.model;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.api.model.AbstractModelComponent;
import com.sun.jersey.api.model.AbstractModelVisitor;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.api.model.Parameterized;
import com.sun.jersey.api.model.PathAnnotated;
import com.sun.jersey.api.model.PathValue;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AbstractSubResourceLocator
extends AbstractMethod
implements PathAnnotated,
Parameterized,
AbstractModelComponent {
    private PathValue uriPath;
    private List<Parameter> parameters;

    public AbstractSubResourceLocator(AbstractResource resource, Method method, PathValue uriPath, Annotation[] annotations) {
        super(resource, method, annotations);
        this.uriPath = uriPath;
        this.parameters = new ArrayList<Parameter>();
    }

    @Override
    public PathValue getPath() {
        return this.uriPath;
    }

    @Override
    public List<Parameter> getParameters() {
        return this.parameters;
    }

    @Override
    public void accept(AbstractModelVisitor visitor) {
        visitor.visitAbstractSubResourceLocator(this);
    }

    @Override
    public List<AbstractModelComponent> getComponents() {
        return null;
    }

    public String toString() {
        return "AbstractSubResourceLocator(" + this.getMethod().getDeclaringClass().getSimpleName() + "#" + this.getMethod().getName() + ")";
    }
}

