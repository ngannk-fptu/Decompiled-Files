/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.wrappers;

import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.wrappers.Wrapper;

public class PojoWrapper
extends Wrapper {
    protected MetaClass delegate;
    protected final Object wrapped;

    public PojoWrapper(Object wrapped, Class constrainedType) {
        super(constrainedType);
        this.wrapped = wrapped;
    }

    @Override
    public Object unwrap() {
        return this.wrapped;
    }

    @Override
    public Object getProperty(String property) {
        return this.delegate.getProperty(this.wrapped, property);
    }

    @Override
    public Object invokeMethod(String methodName, Object arguments) {
        return this.delegate.invokeMethod(this.wrapped, methodName, arguments);
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        this.delegate = metaClass;
    }

    @Override
    public void setProperty(String property, Object newValue) {
        this.delegate.setProperty(this.wrapped, property, newValue);
    }

    @Override
    protected Object getWrapped() {
        return this.wrapped;
    }

    @Override
    protected MetaClass getDelegatedMetaClass() {
        return this.delegate;
    }
}

