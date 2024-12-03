/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.healthcheck.checks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FontManagerChecker {
    boolean fontCheck() {
        try {
            Class<?> fontManagerFactoryClass = Class.forName("sun.font.FontManagerFactory");
            Method getInstanceMethod = fontManagerFactoryClass.getMethod("getInstance", new Class[0]);
            getInstanceMethod.invoke(null, new Object[0]);
            return true;
        }
        catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            return false;
        }
    }
}

