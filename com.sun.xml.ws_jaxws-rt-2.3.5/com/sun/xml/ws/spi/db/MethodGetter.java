/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.spi.db;

import com.sun.xml.ws.spi.db.PropertyGetterBase;
import java.lang.reflect.Method;
import javax.xml.ws.WebServiceException;

public class MethodGetter
extends PropertyGetterBase {
    private Method method;

    public MethodGetter(Method m) {
        MethodGetter.verifyWrapperType(m.getDeclaringClass());
        this.method = m;
        this.type = m.getReturnType();
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
    public Object get(Object instance) {
        Object[] args = new Object[]{};
        try {
            return this.method.invoke(instance, args);
        }
        catch (Exception e) {
            throw new WebServiceException((Throwable)e);
        }
    }
}

