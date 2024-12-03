/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.client.sei;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

class MethodUtil {
    private static final Logger LOGGER = Logger.getLogger(MethodUtil.class.getName());

    MethodUtil() {
    }

    static Object invoke(Object target, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Invoking method using com.sun.xml.ws.util.MethodUtil");
        }
        try {
            return com.sun.xml.ws.util.MethodUtil.invoke(method, target, args);
        }
        catch (InvocationTargetException ite) {
            throw MethodUtil.unwrapException(ite);
        }
    }

    private static InvocationTargetException unwrapException(InvocationTargetException ite) {
        Throwable targetException = ite.getTargetException();
        if (targetException != null && targetException instanceof InvocationTargetException) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Unwrapping invocation target exception");
            }
            return (InvocationTargetException)targetException;
        }
        return ite;
    }
}

