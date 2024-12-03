/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util.introspection;

import java.lang.reflect.Method;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.util.introspection.Introspector;
import org.apache.velocity.util.introspection.SecureIntrospectorControl;

public class SecureIntrospectorImpl
extends Introspector
implements SecureIntrospectorControl {
    private String[] badClasses;
    private String[] badPackages;

    public SecureIntrospectorImpl(String[] badClasses, String[] badPackages, Log log) {
        super(log);
        this.badClasses = badClasses;
        this.badPackages = badPackages;
    }

    public Method getMethod(Class clazz, String methodName, Object[] params) throws IllegalArgumentException {
        if (!this.checkObjectExecutePermission(clazz, methodName)) {
            this.log.warn("Cannot retrieve method " + methodName + " from object of class " + clazz.getName() + " due to security restrictions.");
            return null;
        }
        return super.getMethod(clazz, methodName, params);
    }

    public boolean checkObjectExecutePermission(Class clazz, String methodName) {
        int i;
        int dotPos;
        if (methodName != null && (methodName.equals("wait") || methodName.equals("notify"))) {
            return false;
        }
        if (Number.class.isAssignableFrom(clazz)) {
            return true;
        }
        if (Boolean.class.isAssignableFrom(clazz)) {
            return true;
        }
        if (String.class.isAssignableFrom(clazz)) {
            return true;
        }
        if (Class.class.isAssignableFrom(clazz) && methodName != null && methodName.equals("getName")) {
            return true;
        }
        String className = clazz.getName();
        if (className.startsWith("[L") && className.endsWith(";")) {
            className = className.substring(2, className.length() - 1);
        }
        String packageName = (dotPos = className.lastIndexOf(46)) == -1 ? "" : className.substring(0, dotPos);
        int size = this.badPackages.length;
        for (i = 0; i < size; ++i) {
            if (!packageName.equals(this.badPackages[i])) continue;
            return false;
        }
        size = this.badClasses.length;
        for (i = 0; i < size; ++i) {
            if (!className.equals(this.badClasses[i])) continue;
            return false;
        }
        return true;
    }
}

