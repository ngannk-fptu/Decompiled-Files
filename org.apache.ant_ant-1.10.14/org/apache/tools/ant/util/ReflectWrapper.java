/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.lang.reflect.Constructor;
import org.apache.tools.ant.util.ReflectUtil;

public class ReflectWrapper {
    private Object obj;

    public ReflectWrapper(ClassLoader loader, String name) {
        try {
            Class<?> clazz = Class.forName(name, true, loader);
            Constructor<?> constructor = clazz.getConstructor(new Class[0]);
            this.obj = constructor.newInstance(new Object[0]);
        }
        catch (Exception t) {
            ReflectUtil.throwBuildException(t);
        }
    }

    public ReflectWrapper(Object obj) {
        this.obj = obj;
    }

    public <T> T getObject() {
        return (T)this.obj;
    }

    public <T> T invoke(String methodName) {
        return ReflectUtil.invoke(this.obj, methodName);
    }

    public <T> T invoke(String methodName, Class<?> argType, Object arg) {
        return ReflectUtil.invoke(this.obj, methodName, argType, arg);
    }

    public <T> T invoke(String methodName, Class<?> argType1, Object arg1, Class<?> argType2, Object arg2) {
        return ReflectUtil.invoke(this.obj, methodName, argType1, arg1, argType2, arg2);
    }
}

