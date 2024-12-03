/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import java.lang.reflect.Method;
import org.codehaus.groovy.runtime.InvokerHelper;

public class ReflectionMethodInvoker {
    public static Object invoke(Object object, String methodName, Object[] parameters) {
        try {
            Class[] classTypes = new Class[parameters.length];
            for (int i = 0; i < classTypes.length; ++i) {
                classTypes[i] = parameters[i].getClass();
            }
            Method method = object.getClass().getMethod(methodName, classTypes);
            return method.invoke(object, parameters);
        }
        catch (Throwable t) {
            return InvokerHelper.invokeMethod(object, methodName, parameters);
        }
    }
}

