/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class EntityDtoConverter<A, B> {
    public abstract void copyDtoToEntity(A var1, B var2);

    public abstract A entityToDto(B var1);

    public B dtoToEntity(A src) {
        B dest;
        try {
            dest = this.getTypeParameterClass().newInstance();
        }
        catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalArgumentException(e);
        }
        this.copyDtoToEntity(src, dest);
        return dest;
    }

    private Class<B> getTypeParameterClass() {
        Type type = this.getClass().getGenericSuperclass();
        ParameterizedType paramType = (ParameterizedType)type;
        return (Class)paramType.getActualTypeArguments()[1];
    }
}

