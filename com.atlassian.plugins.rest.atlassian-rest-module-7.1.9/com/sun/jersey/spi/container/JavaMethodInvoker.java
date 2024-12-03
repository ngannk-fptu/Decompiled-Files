/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.container;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface JavaMethodInvoker {
    public Object invoke(Method var1, Object var2, Object ... var3) throws InvocationTargetException, IllegalAccessException;
}

