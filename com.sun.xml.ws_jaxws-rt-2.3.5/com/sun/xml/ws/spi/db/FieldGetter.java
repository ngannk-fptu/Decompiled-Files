/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.spi.db;

import com.sun.xml.ws.spi.db.PropertyGetterBase;
import java.lang.reflect.Field;
import javax.xml.ws.WebServiceException;

public class FieldGetter
extends PropertyGetterBase {
    protected Field field;

    public FieldGetter(Field f) {
        FieldGetter.verifyWrapperType(f.getDeclaringClass());
        this.field = f;
        this.type = f.getType();
    }

    public Field getField() {
        return this.field;
    }

    @Override
    public Object get(Object instance) {
        try {
            return this.field.get(instance);
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

