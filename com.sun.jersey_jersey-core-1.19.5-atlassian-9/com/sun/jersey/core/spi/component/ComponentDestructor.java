/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.component;

import com.sun.jersey.core.reflection.AnnotatedMethod;
import com.sun.jersey.core.reflection.MethodList;
import com.sun.jersey.core.reflection.ReflectionHelper;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ComponentDestructor {
    private final List<Method> preDestroys;

    public ComponentDestructor(Class c) {
        this.preDestroys = ComponentDestructor.getPreDestroyMethods(c);
    }

    private static List<Method> getPreDestroyMethods(Class c) {
        Class<?> preDestroyClass = AccessController.doPrivileged(ReflectionHelper.classForNamePA("javax.annotation.PreDestroy"));
        ArrayList<Method> list = new ArrayList<Method>();
        HashSet<String> names = new HashSet<String>();
        if (preDestroyClass != null) {
            MethodList methodList = new MethodList(c, true);
            for (AnnotatedMethod m : methodList.hasAnnotation(preDestroyClass).hasNumParams(0).hasReturnType(Void.TYPE)) {
                Method method = m.getMethod();
                if (!names.add(method.getName())) continue;
                AccessController.doPrivileged(ReflectionHelper.setAccessibleMethodPA(method));
                list.add(method);
            }
        }
        return list;
    }

    public void destroy(Object o) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for (Method preDestroy : this.preDestroys) {
            preDestroy.invoke(o, new Object[0]);
        }
    }
}

