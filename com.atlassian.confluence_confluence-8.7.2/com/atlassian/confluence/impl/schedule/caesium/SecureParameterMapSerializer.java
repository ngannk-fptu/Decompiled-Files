/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.core.util.ParameterMapSerializer
 *  com.google.common.collect.ImmutableMap
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.schedule.caesium;

import com.atlassian.confluence.impl.schedule.caesium.SecureClassLoaderAwareObjectInputStream;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.core.util.ParameterMapSerializer;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.Nullable;

class SecureParameterMapSerializer
extends ParameterMapSerializer {
    private static final ClassLoader CLASS_LOADER = SecureParameterMapSerializer.class.getClassLoader();
    private final Set<String> parameterClassWhiteList;

    public SecureParameterMapSerializer(Set<String> parameterClassWhiteList) {
        this.parameterClassWhiteList = parameterClassWhiteList;
    }

    public @Nullable byte[] serializeParameters(@Nullable Map<String, Serializable> parameters) throws SchedulerServiceException {
        if (parameters == null) {
            return null;
        }
        ImmutableMap immutableMap = ImmutableMap.copyOf(parameters);
        byte[] serialized = super.serializeParameters((Map)immutableMap);
        try {
            this.deserializeParameters(CLASS_LOADER, serialized);
        }
        catch (SecureClassLoaderAwareObjectInputStream.DisallowedClassException e) {
            throw new SchedulerServiceException("Unexpected class: " + e.getDisallowedClass() + ". For security reason, only these class types are allowed in clustered job parameters: " + this.parameterClassWhiteList);
        }
        catch (IOException | ClassNotFoundException e) {
            throw new SchedulerServiceException("Serialized parameters cannot be deserialized later", (Throwable)e);
        }
        return serialized;
    }

    protected ObjectInputStream createObjectInputStream(ClassLoader classLoader, byte[] parameters) throws IOException {
        return new SecureClassLoaderAwareObjectInputStream(classLoader, parameters, className -> "com.google.common.collect.ImmutableMap$SerializedForm".equals(className) || "com.google.common.collect.ImmutableBiMap$SerializedForm".equals(className) || "com.google.common.collect.RegularImmutableMap$SerializedForm".equals(className) || "[Ljava.lang.Object;".equals(className) || this.parameterClassWhiteList.contains(className));
    }
}

