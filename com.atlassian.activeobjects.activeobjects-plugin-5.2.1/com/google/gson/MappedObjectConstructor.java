/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.DefaultConstructorAllocator;
import com.google.gson.InstanceCreator;
import com.google.gson.ObjectConstructor;
import com.google.gson.ParameterizedTypeHandlerMap;
import com.google.gson.UnsafeAllocator;
import com.google.gson.internal.$Gson$Types;
import java.lang.reflect.Array;
import java.lang.reflect.Type;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class MappedObjectConstructor
implements ObjectConstructor {
    private static final UnsafeAllocator unsafeAllocator = UnsafeAllocator.create();
    private static final DefaultConstructorAllocator defaultConstructorAllocator = new DefaultConstructorAllocator(500);
    private final ParameterizedTypeHandlerMap<InstanceCreator<?>> instanceCreatorMap;

    public MappedObjectConstructor(ParameterizedTypeHandlerMap<InstanceCreator<?>> instanceCreators) {
        this.instanceCreatorMap = instanceCreators;
    }

    @Override
    public <T> T construct(Type typeOfT) {
        InstanceCreator<?> creator = this.instanceCreatorMap.getHandlerFor(typeOfT);
        if (creator != null) {
            return (T)creator.createInstance(typeOfT);
        }
        return this.constructWithAllocators(typeOfT);
    }

    @Override
    public Object constructArray(Type type, int length) {
        return Array.newInstance($Gson$Types.getRawType(type), length);
    }

    private <T> T constructWithAllocators(Type typeOfT) {
        try {
            Class<?> clazz = $Gson$Types.getRawType(typeOfT);
            Object obj = defaultConstructorAllocator.newInstance(clazz);
            return (T)(obj == null ? unsafeAllocator.newInstance(clazz) : obj);
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to invoke no-args constructor for " + typeOfT + ". " + "Register an InstanceCreator with Gson for this type may fix this problem.", e);
        }
    }

    public String toString() {
        return this.instanceCreatorMap.toString();
    }
}

