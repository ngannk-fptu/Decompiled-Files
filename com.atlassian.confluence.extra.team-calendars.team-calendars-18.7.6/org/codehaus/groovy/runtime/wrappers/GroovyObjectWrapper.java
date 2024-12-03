/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.wrappers;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.wrappers.Wrapper;

public class GroovyObjectWrapper
extends Wrapper {
    protected final GroovyObject wrapped;

    public GroovyObjectWrapper(GroovyObject wrapped, Class constrainedType) {
        super(constrainedType);
        this.wrapped = wrapped;
    }

    @Override
    public Object unwrap() {
        return this.wrapped;
    }

    @Override
    public Object getProperty(String property) {
        return this.wrapped.getProperty(property);
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        return this.wrapped.invokeMethod(name, args);
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        this.wrapped.setMetaClass(metaClass);
    }

    @Override
    public void setProperty(String property, Object newValue) {
        this.wrapped.setProperty(property, newValue);
    }

    @Override
    protected Object getWrapped() {
        return this.wrapped;
    }

    @Override
    protected MetaClass getDelegatedMetaClass() {
        return this.wrapped.getMetaClass();
    }
}

