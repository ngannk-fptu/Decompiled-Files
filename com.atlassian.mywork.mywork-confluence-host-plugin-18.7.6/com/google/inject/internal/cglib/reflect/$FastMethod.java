/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.cglib.reflect;

import com.google.inject.internal.cglib.reflect.$FastClass;
import com.google.inject.internal.cglib.reflect.$FastMember;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class $FastMethod
extends $FastMember {
    $FastMethod($FastClass fc, Method method) {
        super(fc, method, $FastMethod.helper(fc, method));
    }

    private static int helper($FastClass fc, Method method) {
        int index = fc.getIndex(method.getName(), method.getParameterTypes());
        if (index < 0) {
            Class<?>[] types = method.getParameterTypes();
            System.err.println("hash=" + method.getName().hashCode() + " size=" + types.length);
            for (int i = 0; i < types.length; ++i) {
                System.err.println("  types[" + i + "]=" + types[i].getName());
            }
            throw new IllegalArgumentException("Cannot find method " + method);
        }
        return index;
    }

    public Class getReturnType() {
        return ((Method)this.member).getReturnType();
    }

    public Class[] getParameterTypes() {
        return ((Method)this.member).getParameterTypes();
    }

    public Class[] getExceptionTypes() {
        return ((Method)this.member).getExceptionTypes();
    }

    public Object invoke(Object obj, Object[] args) throws InvocationTargetException {
        return this.fc.invoke(this.index, obj, args);
    }

    public Method getJavaMethod() {
        return (Method)this.member;
    }
}

