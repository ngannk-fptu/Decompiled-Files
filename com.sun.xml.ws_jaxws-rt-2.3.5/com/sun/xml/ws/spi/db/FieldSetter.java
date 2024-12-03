/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.spi.db;

import com.sun.xml.ws.spi.db.PropertyGetterBase;
import com.sun.xml.ws.spi.db.PropertySetterBase;
import java.lang.reflect.Field;
import javax.xml.ws.WebServiceException;

public class FieldSetter
extends PropertySetterBase {
    protected Field field;

    public FieldSetter(Field f) {
        PropertyGetterBase.verifyWrapperType(f.getDeclaringClass());
        this.field = f;
        this.type = f.getType();
    }

    public Field getField() {
        return this.field;
    }

    @Override
    public void set(Object instance, Object val) {
        Object resource = this.type.isPrimitive() && val == null ? FieldSetter.uninitializedValue(this.type) : val;
        try {
            this.field.set(instance, resource);
        }
        catch (Exception e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    @Override
    public <A> A getAnnotation(Class<A> annotationType) {
        Class<A> c = annotationType;
        return this.field.getAnnotation(c);
    }
}

