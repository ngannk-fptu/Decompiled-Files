/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.wrappers;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;

public abstract class Wrapper
implements GroovyObject {
    protected final Class constrainedType;

    public Wrapper(Class constrainedType) {
        this.constrainedType = constrainedType;
    }

    @Override
    public MetaClass getMetaClass() {
        return this.getDelegatedMetaClass();
    }

    public Class getType() {
        return this.constrainedType;
    }

    public abstract Object unwrap();

    protected abstract Object getWrapped();

    protected abstract MetaClass getDelegatedMetaClass();
}

