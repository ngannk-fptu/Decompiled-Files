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
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AbstractSetterMethod
extends AbstractMethod
implements Parameterized,
AbstractModelComponent {
    private List<Parameter> parameters = new ArrayList<Parameter>();

    public AbstractSetterMethod(AbstractResource resource, Method method, Annotation[] annotations) {
        super(resource, method, annotations);
    }

    @Override
    public List<Parameter> getParameters() {
        return this.parameters;
    }

    @Override
    public void accept(AbstractModelVisitor visitor) {
        visitor.visitAbstractSetterMethod(this);
    }

    @Override
    public List<AbstractModelComponent> getComponents() {
        return null;
    }

    public String toString() {
        return "AbstractSetterMethod(" + this.getMethod().getDeclaringClass().getSimpleName() + "#" + this.getMethod().getName() + ")";
    }
}

