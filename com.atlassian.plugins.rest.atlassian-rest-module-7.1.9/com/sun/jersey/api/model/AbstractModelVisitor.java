/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.model;

import com.sun.jersey.api.model.AbstractField;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractResourceConstructor;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.AbstractSetterMethod;
import com.sun.jersey.api.model.AbstractSubResourceLocator;
import com.sun.jersey.api.model.AbstractSubResourceMethod;

public interface AbstractModelVisitor {
    public void visitAbstractResource(AbstractResource var1);

    public void visitAbstractField(AbstractField var1);

    public void visitAbstractSetterMethod(AbstractSetterMethod var1);

    public void visitAbstractResourceMethod(AbstractResourceMethod var1);

    public void visitAbstractSubResourceMethod(AbstractSubResourceMethod var1);

    public void visitAbstractSubResourceLocator(AbstractSubResourceLocator var1);

    public void visitAbstractResourceConstructor(AbstractResourceConstructor var1);
}

