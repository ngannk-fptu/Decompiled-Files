/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.model;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.api.model.AbstractResource;

public class AbstractImplicitViewMethod
extends AbstractMethod {
    public AbstractImplicitViewMethod(AbstractResource resource) {
        super(resource, null, resource.getAnnotations());
    }

    public String toString() {
        return "AbstractImplicitViewMethod(" + this.getResource().getResourceClass().getSimpleName() + ")";
    }
}

