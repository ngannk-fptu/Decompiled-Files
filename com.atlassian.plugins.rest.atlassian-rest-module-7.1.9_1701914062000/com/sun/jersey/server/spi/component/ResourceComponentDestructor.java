/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.spi.component;

import com.sun.jersey.api.model.AbstractResource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ResourceComponentDestructor {
    private final List<Method> preDestroys = new ArrayList<Method>();

    public ResourceComponentDestructor(AbstractResource ar) {
        this.preDestroys.addAll(ar.getPreDestroyMethods());
    }

    public void destroy(Object o) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for (Method preDestroy : this.preDestroys) {
            preDestroy.invoke(o, new Object[0]);
        }
    }
}

