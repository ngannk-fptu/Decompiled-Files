/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import org.mozilla.javascript.JavaMembers;
import org.mozilla.javascript.Scriptable;

class JavaMembers_jdk11
extends JavaMembers {
    JavaMembers_jdk11(Scriptable scope, Class<?> cl, boolean includeProtected) {
        super(scope, cl, includeProtected);
    }

    @Override
    void discoverPublicMethods(Class<?> clazz, Map<JavaMembers.MethodSignature, Method> map) {
        if (JavaMembers_jdk11.isExportedClass(clazz)) {
            super.discoverPublicMethods(clazz, map);
        } else {
            Method[] methods;
            for (Method method : methods = clazz.getMethods()) {
                method = JavaMembers_jdk11.findAccessibleMethod(method);
                JavaMembers_jdk11.registerMethod(map, method);
            }
        }
    }

    private static boolean isExportedClass(Class<?> clazz) {
        boolean exported;
        Object module;
        Method getmodule;
        String pname;
        Package pkg = clazz.getPackage();
        if (pkg == null) {
            if (!Proxy.isProxyClass(clazz)) {
                return true;
            }
            String clName = clazz.getName();
            pname = clName.substring(0, clName.lastIndexOf(46));
        } else {
            pname = pkg.getName();
        }
        Class<?> cl = clazz.getClass();
        try {
            getmodule = cl.getMethod("getModule", new Class[0]);
        }
        catch (NoSuchMethodException e) {
            return true;
        }
        try {
            module = getmodule.invoke(clazz, new Object[0]);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            return false;
        }
        Class<?> moduleClass = module.getClass();
        try {
            Method isexported = moduleClass.getMethod("isExported", String.class);
            exported = (Boolean)isexported.invoke(module, pname);
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException e) {
            exported = false;
        }
        return exported;
    }

    /*
     * Unable to fully structure code
     */
    private static Method findAccessibleMethod(Method method) {
        cl = method.getDeclaringClass();
        methodName = method.getName();
        methodTypes = method.getParameterTypes();
        block4: while (true) {
            for (Class<?> intface : cl.getInterfaces()) {
                try {
                    method = intface.getMethod(methodName, methodTypes);
                    break block4;
                }
                catch (NoSuchMethodException var8_9) {
                }
            }
            if ((cl = cl.getSuperclass()) == null) break;
            if (!JavaMembers_jdk11.isExportedClass(cl)) ** continue;
            try {
                method = cl.getMethod(methodName, methodTypes);
            }
            catch (NoSuchMethodException var4_5) {
                continue;
            }
            break;
        }
        return method;
    }
}

