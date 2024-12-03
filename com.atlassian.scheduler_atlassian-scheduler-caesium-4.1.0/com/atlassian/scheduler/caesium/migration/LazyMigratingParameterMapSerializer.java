/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.core.util.ParameterMapSerializer
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.scheduler.caesium.migration;

import com.atlassian.scheduler.caesium.migration.LazyMigratingObjectInputStream;
import com.atlassian.scheduler.core.util.ParameterMapSerializer;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LazyMigratingParameterMapSerializer
extends ParameterMapSerializer {
    protected ObjectInputStream createObjectInputStream(ClassLoader classLoader, byte[] parameters) throws IOException {
        return new LazyMigratingObjectInputStream(classLoader, parameters);
    }

    @Nonnull
    public Map<String, Serializable> deserializeParameters(ClassLoader classLoader, @Nullable byte[] parameters) throws ClassNotFoundException, IOException {
        if (parameters != null) {
            Object result = this.deserializeBytes(classLoader, parameters);
            if (result instanceof byte[]) {
                result = this.deserializeBytes(classLoader, (byte[])result);
            }
            if (result != null) {
                return (Map)result;
            }
        }
        return ImmutableMap.of();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    private Object deserializeBytes(ClassLoader classLoader, @Nullable byte[] parameters) throws ClassNotFoundException, IOException {
        if (parameters == null) {
            return ImmutableMap.of();
        }
        try (ObjectInputStream in = this.createObjectInputStream(classLoader, parameters);){
            Object object = in.readObject();
            return object;
        }
    }
}

