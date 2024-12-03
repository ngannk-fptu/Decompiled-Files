/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.hostcontainer.HostContainer
 */
package com.atlassian.plugin.hostcontainer;

import com.atlassian.plugin.hostcontainer.HostContainer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class SimpleConstructorHostContainer
implements HostContainer {
    private final Map<Class<?>, Object> context;

    public SimpleConstructorHostContainer(Map<Class<?>, Object> context) {
        HashMap tmp = new HashMap(context);
        tmp.put(HostContainer.class, this);
        this.context = Collections.unmodifiableMap(tmp);
    }

    public <T> T create(Class<T> moduleClass) {
        for (Constructor<T> constructor : this.findConstructorsLargestFirst(moduleClass)) {
            ArrayList<Object> params = new ArrayList<Object>();
            for (Class<?> paramType : constructor.getParameterTypes()) {
                if (!this.context.containsKey(paramType)) continue;
                params.add(this.context.get(paramType));
            }
            if (constructor.getParameterTypes().length != params.size()) continue;
            try {
                return constructor.newInstance(params.toArray());
            }
            catch (InstantiationException e) {
                throw new IllegalArgumentException(e);
            }
            catch (IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            }
            catch (InvocationTargetException e) {
                throw new IllegalArgumentException(e);
            }
        }
        throw new IllegalArgumentException("Unable to match any constructor for class " + moduleClass);
    }

    private <T> Collection<Constructor<T>> findConstructorsLargestFirst(Class<T> moduleClass) {
        TreeSet<Constructor<T>> constructors = new TreeSet<Constructor<T>>((first, second) -> Integer.valueOf(second.getParameterTypes().length).compareTo(first.getParameterTypes().length));
        for (Constructor<?> constructor : moduleClass.getConstructors()) {
            constructors.add(constructor);
        }
        return constructors;
    }
}

