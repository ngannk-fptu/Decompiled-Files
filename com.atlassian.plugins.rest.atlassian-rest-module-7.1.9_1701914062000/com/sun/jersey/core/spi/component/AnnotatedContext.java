/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.component;

import com.sun.jersey.core.spi.component.ComponentContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;

public class AnnotatedContext
implements ComponentContext {
    private Annotation[] annotations;
    private AccessibleObject ao;

    public AnnotatedContext() {
    }

    public AnnotatedContext(Annotation[] annotations) {
        this(null, annotations);
    }

    public AnnotatedContext(AccessibleObject ao) {
        this(ao, null);
    }

    public AnnotatedContext(AccessibleObject ao, Annotation[] annotations) {
        this.ao = ao;
        this.annotations = annotations;
    }

    public void setAnnotations(Annotation[] annotations) {
        this.annotations = annotations;
    }

    public void setAccessibleObject(AccessibleObject ao) {
        this.ao = ao;
    }

    @Override
    public AccessibleObject getAccesibleObject() {
        return this.ao;
    }

    @Override
    public Annotation[] getAnnotations() {
        return this.annotations;
    }
}

