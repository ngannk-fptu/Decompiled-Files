/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;
import org.apache.xmlrpc.XmlRpc;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;

public class Invoker
implements XmlRpcHandler {
    private Object invokeTarget;
    private Class targetClass;
    static /* synthetic */ Class class$java$lang$Object;

    public Invoker(Object target) {
        this.invokeTarget = target;
        Class<?> clazz = this.targetClass = this.invokeTarget instanceof Class ? (Class<?>)this.invokeTarget : this.invokeTarget.getClass();
        if (XmlRpc.debug) {
            System.out.println("Target object is " + this.targetClass);
        }
    }

    public Object execute(String methodName, Vector params) throws Exception {
        Class[] argClasses = null;
        Object[] argValues = null;
        if (params != null) {
            argClasses = new Class[params.size()];
            argValues = new Object[params.size()];
            for (int i = 0; i < params.size(); ++i) {
                argValues[i] = params.elementAt(i);
                argClasses[i] = argValues[i] instanceof Integer ? Integer.TYPE : (argValues[i] instanceof Double ? Double.TYPE : (argValues[i] instanceof Boolean ? Boolean.TYPE : argValues[i].getClass()));
            }
        }
        Method method = null;
        int dot = methodName.lastIndexOf(46);
        if (dot > -1 && dot + 1 < methodName.length()) {
            methodName = methodName.substring(dot + 1);
        }
        if (XmlRpc.debug) {
            System.out.println("Searching for method: " + methodName + " in class " + this.targetClass.getName());
            for (int i = 0; i < argClasses.length; ++i) {
                System.out.println("Parameter " + i + ": " + argValues[i] + " (" + argClasses[i] + ')');
            }
        }
        try {
            method = this.targetClass.getMethod(methodName, argClasses);
        }
        catch (NoSuchMethodException nsm_e) {
            throw nsm_e;
        }
        catch (SecurityException s_e) {
            throw s_e;
        }
        if (method.getDeclaringClass() == (class$java$lang$Object == null ? (class$java$lang$Object = Invoker.class$("java.lang.Object")) : class$java$lang$Object)) {
            throw new XmlRpcException(0, "Invoker can't call methods defined in java.lang.Object");
        }
        Object returnValue = null;
        try {
            returnValue = method.invoke(this.invokeTarget, argValues);
        }
        catch (IllegalAccessException iacc_e) {
            throw iacc_e;
        }
        catch (IllegalArgumentException iarg_e) {
            throw iarg_e;
        }
        catch (InvocationTargetException it_e) {
            Throwable t;
            if (XmlRpc.debug) {
                it_e.getTargetException().printStackTrace();
            }
            if ((t = it_e.getTargetException()) instanceof XmlRpcException) {
                throw (XmlRpcException)t;
            }
            throw new Exception(t.toString());
        }
        if (returnValue == null && method.getReturnType() == Void.TYPE) {
            throw new IllegalArgumentException("void return types for handler methods not supported");
        }
        return returnValue;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

