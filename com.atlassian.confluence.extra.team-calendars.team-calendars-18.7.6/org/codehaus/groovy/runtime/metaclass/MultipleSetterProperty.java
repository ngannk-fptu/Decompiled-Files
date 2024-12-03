/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.metaclass;

import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaMethod;
import groovy.lang.MetaProperty;
import org.codehaus.groovy.reflection.CachedField;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.MetaClassHelper;

public class MultipleSetterProperty
extends MetaProperty {
    private MetaMethod getter;
    private CachedField field;
    private final String setterName;

    public MultipleSetterProperty(String name) {
        super(name, Object.class);
        this.setterName = MetaProperty.getSetterName(name);
    }

    @Override
    public Object getProperty(Object object) {
        MetaMethod getter = this.getGetter();
        if (getter == null) {
            if (this.field != null) {
                return this.field.getProperty(object);
            }
            throw new GroovyRuntimeException("Cannot read write-only property: " + this.name);
        }
        return getter.invoke(object, MetaClassHelper.EMPTY_ARRAY);
    }

    @Override
    public void setProperty(Object object, Object newValue) {
        InvokerHelper.getMetaClass(object).invokeMethod(object, this.setterName, new Object[]{newValue});
    }

    public void setField(CachedField f) {
        this.field = f;
    }

    public CachedField getField() {
        return this.field;
    }

    public void setGetter(MetaMethod getter) {
        this.getter = getter;
    }

    public MetaMethod getGetter() {
        return this.getter;
    }

    public MetaProperty createStaticVersion() {
        boolean mg;
        boolean mf = this.field == null || this.field.isStatic();
        boolean bl = mg = this.getter == null || this.getter.isStatic();
        if (mf && mg) {
            return this;
        }
        if (mg) {
            MultipleSetterProperty newMsp = new MultipleSetterProperty(this.name);
            newMsp.setGetter(this.getter);
            return newMsp;
        }
        if (mf) {
            MultipleSetterProperty newMsp = new MultipleSetterProperty(this.name);
            newMsp.setField(this.field);
            return newMsp;
        }
        return null;
    }
}

