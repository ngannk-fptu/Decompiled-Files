/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection;

import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaProperty;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class CachedField
extends MetaProperty {
    public final Field field;

    public CachedField(Field field) {
        super(field.getName(), field.getType());
        this.field = field;
    }

    public boolean isStatic() {
        return Modifier.isStatic(this.getModifiers());
    }

    public boolean isFinal() {
        return Modifier.isFinal(this.getModifiers());
    }

    @Override
    public int getModifiers() {
        return this.field.getModifiers();
    }

    @Override
    public Object getProperty(Object object) {
        try {
            return this.field.get(object);
        }
        catch (IllegalAccessException e) {
            throw new GroovyRuntimeException("Cannot get the property '" + this.name + "'.", e);
        }
    }

    @Override
    public void setProperty(Object object, Object newValue) {
        Object goalValue = DefaultTypeTransformation.castToType(newValue, this.field.getType());
        if (this.isFinal()) {
            throw new GroovyRuntimeException("Cannot set the property '" + this.name + "' because the backing field is final.");
        }
        try {
            this.field.set(object, goalValue);
        }
        catch (IllegalAccessException ex) {
            throw new GroovyRuntimeException("Cannot set the property '" + this.name + "'.", ex);
        }
    }
}

