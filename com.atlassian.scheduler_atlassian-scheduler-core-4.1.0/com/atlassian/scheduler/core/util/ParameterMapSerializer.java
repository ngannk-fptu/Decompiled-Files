/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.scheduler.core.util;

import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.core.util.ClassLoaderAwareObjectInputStream;
import com.google.common.collect.ImmutableMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ParameterMapSerializer {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public byte[] serializeParameters(@Nullable Map<String, Serializable> parameters) throws SchedulerServiceException {
        if (parameters == null || parameters.isEmpty()) {
            return null;
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes);){
            out.writeObject(parameters);
        }
        catch (IOException ioe) {
            throw new SchedulerServiceException("Serialization failed", (Throwable)ioe);
        }
        return bytes.toByteArray();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    public Map<String, Serializable> deserializeParameters(ClassLoader classLoader, @Nullable byte[] parameters) throws ClassNotFoundException, IOException {
        if (parameters == null) {
            return ImmutableMap.of();
        }
        try (ObjectInputStream in = this.createObjectInputStream(classLoader, parameters);){
            Map<String, Serializable> map = this.readParameterMap(in);
            return map;
        }
    }

    protected ObjectInputStream createObjectInputStream(ClassLoader classLoader, byte[] parameters) throws IOException {
        return new ClassLoaderAwareObjectInputStream(classLoader, parameters);
    }

    private Map<String, Serializable> readParameterMap(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Map map = (Map)in.readObject();
        if (map != null) {
            return map;
        }
        return ImmutableMap.of();
    }
}

