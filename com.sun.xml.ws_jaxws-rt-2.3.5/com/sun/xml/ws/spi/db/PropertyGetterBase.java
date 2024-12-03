/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.spi.db;

import com.sun.xml.ws.spi.db.PropertyGetter;
import java.lang.reflect.Method;
import javax.xml.ws.WebServiceException;

public abstract class PropertyGetterBase
implements PropertyGetter {
    protected Class type;

    @Override
    public Class getType() {
        return this.type;
    }

    public static boolean getterPattern(Method method) {
        if (!(method.getReturnType().equals(Void.TYPE) || method.getParameterTypes() != null && method.getParameterTypes().length != 0)) {
            if (method.getName().startsWith("get") && method.getName().length() > 3) {
                return true;
            }
            if ((method.getReturnType().equals(Boolean.TYPE) || method.getReturnType().equals(Boolean.class)) && method.getName().startsWith("is") && method.getName().length() > 2) {
                return true;
            }
        }
        return false;
    }

    static void verifyWrapperType(Class wrapperType) {
        String className = wrapperType.getName();
        if (className.startsWith("java.") || className.startsWith("javax.")) {
            throw new WebServiceException("Invalid wrapper type " + className);
        }
    }
}

