/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.Pair;
import com.google.gson.internal.$Gson$Types;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class ParameterizedTypeHandlerMap<T> {
    private static final Logger logger = Logger.getLogger(ParameterizedTypeHandlerMap.class.getName());
    private final Map<Type, T> map = new HashMap<Type, T>();
    private final List<Pair<Class<?>, T>> typeHierarchyList = new ArrayList();
    private boolean modifiable = true;

    ParameterizedTypeHandlerMap() {
    }

    public synchronized void registerForTypeHierarchy(Class<?> typeOfT, T value) {
        Pair pair = new Pair(typeOfT, value);
        this.registerForTypeHierarchy(pair);
    }

    public synchronized void registerForTypeHierarchy(Pair<Class<?>, T> pair) {
        if (!this.modifiable) {
            throw new IllegalStateException("Attempted to modify an unmodifiable map.");
        }
        int index = this.getIndexOfSpecificHandlerForTypeHierarchy((Class)pair.first);
        if (index >= 0) {
            logger.log(Level.WARNING, "Overriding the existing type handler for {0}", pair.first);
            this.typeHierarchyList.remove(index);
        }
        if ((index = this.getIndexOfAnOverriddenHandler((Class)pair.first)) >= 0) {
            throw new IllegalArgumentException("The specified type handler for type " + pair.first + " hides the previously registered type hierarchy handler for " + this.typeHierarchyList.get((int)index).first + ". Gson does not allow this.");
        }
        this.typeHierarchyList.add(0, pair);
    }

    private int getIndexOfAnOverriddenHandler(Class<?> type) {
        for (int i = this.typeHierarchyList.size() - 1; i >= 0; --i) {
            Pair<Class<?>, T> entry = this.typeHierarchyList.get(i);
            if (!type.isAssignableFrom((Class)entry.first)) continue;
            return i;
        }
        return -1;
    }

    public synchronized void register(Type typeOfT, T value) {
        if (!this.modifiable) {
            throw new IllegalStateException("Attempted to modify an unmodifiable map.");
        }
        if (this.hasSpecificHandlerFor(typeOfT)) {
            logger.log(Level.WARNING, "Overriding the existing type handler for {0}", typeOfT);
        }
        this.map.put(typeOfT, value);
    }

    public synchronized void registerIfAbsent(ParameterizedTypeHandlerMap<T> other) {
        if (!this.modifiable) {
            throw new IllegalStateException("Attempted to modify an unmodifiable map.");
        }
        for (Map.Entry<Type, T> entry : other.map.entrySet()) {
            if (this.map.containsKey(entry.getKey())) continue;
            this.register(entry.getKey(), entry.getValue());
        }
        for (int i = other.typeHierarchyList.size() - 1; i >= 0; --i) {
            Map.Entry<Type, T> entry;
            entry = other.typeHierarchyList.get(i);
            int index = this.getIndexOfSpecificHandlerForTypeHierarchy((Class)((Pair)((Object)entry)).first);
            if (index >= 0) continue;
            this.registerForTypeHierarchy((Pair<Class<?>, T>)((Object)entry));
        }
    }

    public synchronized void register(ParameterizedTypeHandlerMap<T> other) {
        if (!this.modifiable) {
            throw new IllegalStateException("Attempted to modify an unmodifiable map.");
        }
        for (Map.Entry<Type, T> entry : other.map.entrySet()) {
            this.register(entry.getKey(), entry.getValue());
        }
        for (int i = other.typeHierarchyList.size() - 1; i >= 0; --i) {
            Pair<Class<?>, T> pair = other.typeHierarchyList.get(i);
            this.registerForTypeHierarchy(pair);
        }
    }

    public synchronized void registerIfAbsent(Type typeOfT, T value) {
        if (!this.modifiable) {
            throw new IllegalStateException("Attempted to modify an unmodifiable map.");
        }
        if (!this.map.containsKey(typeOfT)) {
            this.register(typeOfT, value);
        }
    }

    public synchronized void makeUnmodifiable() {
        this.modifiable = false;
    }

    public synchronized T getHandlerFor(Type type) {
        T handler = this.map.get(type);
        if (handler == null) {
            Class<?> rawClass = $Gson$Types.getRawType(type);
            if (rawClass != type) {
                handler = this.getHandlerFor(rawClass);
            }
            if (handler == null) {
                handler = this.getHandlerForTypeHierarchy(rawClass);
            }
        }
        return handler;
    }

    private T getHandlerForTypeHierarchy(Class<?> type) {
        for (Pair<Class<?>, T> entry : this.typeHierarchyList) {
            if (!((Class)entry.first).isAssignableFrom(type)) continue;
            return (T)entry.second;
        }
        return null;
    }

    public synchronized boolean hasSpecificHandlerFor(Type type) {
        return this.map.containsKey(type);
    }

    private synchronized int getIndexOfSpecificHandlerForTypeHierarchy(Class<?> type) {
        for (int i = this.typeHierarchyList.size() - 1; i >= 0; --i) {
            if (!type.equals(this.typeHierarchyList.get((int)i).first)) continue;
            return i;
        }
        return -1;
    }

    public synchronized ParameterizedTypeHandlerMap<T> copyOf() {
        ParameterizedTypeHandlerMap<T> copy = new ParameterizedTypeHandlerMap<T>();
        copy.map.putAll(this.map);
        copy.typeHierarchyList.addAll(this.typeHierarchyList);
        return copy;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("{mapForTypeHierarchy:{");
        boolean first = true;
        for (Pair<Class<?>, T> pair : this.typeHierarchyList) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            sb.append(this.typeToString((Type)pair.first)).append(':');
            sb.append(pair.second);
        }
        sb.append("},map:{");
        first = true;
        for (Map.Entry entry : this.map.entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            sb.append(this.typeToString((Type)entry.getKey())).append(':');
            sb.append(entry.getValue());
        }
        sb.append("}");
        return sb.toString();
    }

    private String typeToString(Type type) {
        return $Gson$Types.getRawType(type).getSimpleName();
    }
}

