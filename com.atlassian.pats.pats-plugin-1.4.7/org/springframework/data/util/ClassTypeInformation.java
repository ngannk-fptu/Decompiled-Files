/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.GenericTypeResolver
 *  org.springframework.util.Assert
 *  org.springframework.util.ConcurrentReferenceHashMap
 *  org.springframework.util.ConcurrentReferenceHashMap$ReferenceType
 */
package org.springframework.data.util;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.util.ProxyUtils;
import org.springframework.data.util.TypeDiscoverer;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

public class ClassTypeInformation<S>
extends TypeDiscoverer<S> {
    public static final ClassTypeInformation<Collection> COLLECTION = new ClassTypeInformation<Collection>(Collection.class);
    public static final ClassTypeInformation<List> LIST = new ClassTypeInformation<List>(List.class);
    public static final ClassTypeInformation<Set> SET = new ClassTypeInformation<Set>(Set.class);
    public static final ClassTypeInformation<Map> MAP = new ClassTypeInformation<Map>(Map.class);
    public static final ClassTypeInformation<Object> OBJECT = new ClassTypeInformation<Object>(Object.class);
    private static final Map<Class<?>, ClassTypeInformation<?>> cache = new ConcurrentReferenceHashMap(64, ConcurrentReferenceHashMap.ReferenceType.WEAK);
    private final Class<S> type;

    public static <S> ClassTypeInformation<S> from(Class<S> type) {
        Assert.notNull(type, (String)"Type must not be null!");
        return cache.computeIfAbsent(type, ClassTypeInformation::new);
    }

    public static <S> TypeInformation<S> fromReturnTypeOf(Method method) {
        Assert.notNull((Object)method, (String)"Method must not be null!");
        return ClassTypeInformation.from(method.getDeclaringClass()).createInfo(method.getGenericReturnType());
    }

    ClassTypeInformation(Class<S> type) {
        super(ProxyUtils.getUserClass(type), ClassTypeInformation.getTypeVariableMap(type));
        this.type = type;
    }

    private static Map<TypeVariable<?>, Type> getTypeVariableMap(Class<?> type) {
        return ClassTypeInformation.getTypeVariableMap(type, new HashSet<Type>());
    }

    private static Map<TypeVariable<?>, Type> getTypeVariableMap(Class<?> type, Collection<Type> visited) {
        if (visited.contains(type)) {
            return Collections.emptyMap();
        }
        visited.add(type);
        Map source = GenericTypeResolver.getTypeVariableMap(type);
        HashMap map = new HashMap(source.size());
        for (Map.Entry entry : source.entrySet()) {
            Type value = (Type)entry.getValue();
            map.put((TypeVariable<?>)entry.getKey(), (Type)entry.getValue());
            if (!(value instanceof Class)) continue;
            for (Map.Entry<TypeVariable<?>, Type> nestedEntry : ClassTypeInformation.getTypeVariableMap((Class)value, visited).entrySet()) {
                if (map.containsKey(nestedEntry.getKey())) continue;
                map.put(nestedEntry.getKey(), nestedEntry.getValue());
            }
        }
        return map;
    }

    @Override
    public Class<S> getType() {
        return this.type;
    }

    @Override
    public ClassTypeInformation<?> getRawTypeInformation() {
        return this;
    }

    @Override
    public boolean isAssignableFrom(TypeInformation<?> target) {
        return this.getType().isAssignableFrom(target.getType());
    }

    @Override
    public TypeInformation<? extends S> specialize(ClassTypeInformation<?> type) {
        return type;
    }

    public String toString() {
        return this.type.getName();
    }

    static {
        Arrays.asList(COLLECTION, LIST, SET, MAP, OBJECT).forEach(it -> cache.put(it.getType(), (ClassTypeInformation<?>)it));
    }
}

