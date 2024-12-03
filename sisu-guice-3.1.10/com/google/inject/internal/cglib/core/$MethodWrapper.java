/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.cglib.core;

import com.google.inject.internal.cglib.core.$KeyFactory;
import com.google.inject.internal.cglib.core.$ReflectUtils;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class $MethodWrapper {
    private static final MethodWrapperKey KEY_FACTORY = (MethodWrapperKey)((Object)$KeyFactory.create(class$net$sf$cglib$core$MethodWrapper$MethodWrapperKey == null ? (class$net$sf$cglib$core$MethodWrapper$MethodWrapperKey = $MethodWrapper.class$("com.google.inject.internal.cglib.core.$MethodWrapper$MethodWrapperKey")) : class$net$sf$cglib$core$MethodWrapper$MethodWrapperKey));
    static /* synthetic */ Class class$net$sf$cglib$core$MethodWrapper$MethodWrapperKey;

    private $MethodWrapper() {
    }

    public static Object create(Method method) {
        return KEY_FACTORY.newInstance(method.getName(), $ReflectUtils.getNames(method.getParameterTypes()), method.getReturnType().getName());
    }

    public static Set createSet(Collection methods) {
        HashSet<Object> set = new HashSet<Object>();
        Iterator it = methods.iterator();
        while (it.hasNext()) {
            set.add($MethodWrapper.create((Method)it.next()));
        }
        return set;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    public static interface MethodWrapperKey {
        public Object newInstance(String var1, String[] var2, String var3);
    }
}

