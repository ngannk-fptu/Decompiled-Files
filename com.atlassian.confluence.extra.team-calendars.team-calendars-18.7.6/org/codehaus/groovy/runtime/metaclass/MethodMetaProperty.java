/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.metaclass;

import groovy.lang.MetaClassImpl;
import groovy.lang.MetaMethod;
import groovy.lang.MetaProperty;

public class MethodMetaProperty
extends MetaProperty {
    private final MetaMethod method;

    public MethodMetaProperty(String name, MetaMethod method) {
        super(name, Object.class);
        this.method = method;
    }

    @Override
    public Object getProperty(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setProperty(Object object, Object newValue) {
        throw new UnsupportedOperationException();
    }

    public MetaMethod getMetaMethod() {
        return this.method;
    }

    public static class GetBeanMethodMetaProperty
    extends MethodMetaProperty {
        public GetBeanMethodMetaProperty(String name, MetaMethod theMethod) {
            super(name, theMethod);
        }

        @Override
        public Object getProperty(Object object) {
            return this.getMetaMethod().doMethodInvoke(object, MetaClassImpl.EMPTY_ARGUMENTS);
        }
    }

    public static class GetMethodMetaProperty
    extends MethodMetaProperty {
        public GetMethodMetaProperty(String name, MetaMethod theMethod) {
            super(name, theMethod);
        }

        @Override
        public Object getProperty(Object object) {
            return this.getMetaMethod().doMethodInvoke(object, new Object[]{this.name});
        }
    }
}

