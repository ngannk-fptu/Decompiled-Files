/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.JaiI18N;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import javax.media.jai.remote.SerializableState;

class InterfaceHandler
implements InvocationHandler {
    private Hashtable interfaceMap;

    public InterfaceHandler(Class[] interfaces, SerializableState[] implementations) {
        if (interfaces == null || implementations == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (interfaces.length != implementations.length) {
            throw new IllegalArgumentException(JaiI18N.getString("InterfaceHandler0"));
        }
        int numInterfaces = interfaces.length;
        this.interfaceMap = new Hashtable(numInterfaces);
        for (int i = 0; i < numInterfaces; ++i) {
            Class iface = interfaces[i];
            SerializableState state = implementations[i];
            if (!iface.isAssignableFrom(state.getObjectClass())) {
                throw new RuntimeException(JaiI18N.getString("InterfaceHandler1"));
            }
            Object impl = state.getObject();
            this.interfaceMap.put(iface, impl);
        }
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
        Class<?> key = method.getDeclaringClass();
        if (!this.interfaceMap.containsKey(key)) {
            Class[] classes = this.interfaceMap.keySet().toArray(new Class[0]);
            for (int i = 0; i < classes.length; ++i) {
                Class aClass = classes[i];
                if (!key.isAssignableFrom(aClass)) continue;
                this.interfaceMap.put(key, this.interfaceMap.get(aClass));
                break;
            }
            if (!this.interfaceMap.containsKey(key)) {
                throw new RuntimeException(key.getName() + JaiI18N.getString("InterfaceHandler2"));
            }
        }
        Object result = null;
        try {
            Object impl = this.interfaceMap.get(key);
            result = method.invoke(impl, args);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(method.getName() + JaiI18N.getString("InterfaceHandler3"));
        }
        return result;
    }
}

