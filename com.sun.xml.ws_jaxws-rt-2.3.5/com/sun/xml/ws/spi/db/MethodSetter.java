/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.spi.db;

import com.sun.xml.ws.spi.db.PropertyGetterBase;
import com.sun.xml.ws.spi.db.PropertySetterBase;
import java.lang.reflect.Method;
import javax.xml.ws.WebServiceException;

public class MethodSetter
extends PropertySetterBase {
    private Method method;

    public MethodSetter(Method m) {
        PropertyGetterBase.verifyWrapperType(m.getDeclaringClass());
        this.method = m;
        this.type = m.getParameterTypes()[0];
    }

    public Method getMethod() {
        return this.method;
    }

    @Override
    public <A> A getAnnotation(Class<A> annotationType) {
        Class<A> c = annotationType;
        return this.method.getAnnotation(c);
    }

    @Override
    public void set(Object instance, Object val) {
        Object resource = this.type.isPrimitive() && val == null ? MethodSetter.uninitializedValue(this.type) : val;
        Object[] args = new Object[]{resource};
        try {
            this.method.invoke(instance, args);
        }
        catch (Exception e) {
            throw new WebServiceException((Throwable)e);
        }
    }
}

