/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Throwables
 *  com.hazelcast.internal.serialization.InternalSerializationService
 *  com.hazelcast.nio.ObjectDataInput
 *  com.hazelcast.nio.ObjectDataOutput
 *  com.hazelcast.nio.serialization.StreamSerializer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.hazelcast.serialization;

import com.atlassian.hazelcast.serialization.OsgiClassLoaderRegistry;
import com.atlassian.hazelcast.serialization.OsgiSafe;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.CustomClassLoaderObjectDataInput;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsgiSafeStreamSerializer
implements StreamSerializer<OsgiSafe> {
    private static final Logger log = LoggerFactory.getLogger(OsgiSafeStreamSerializer.class);
    private static final int TYPE_ID = 1024;
    private OsgiClassLoaderRegistry registry;
    private InternalSerializationService serializationService;
    private volatile boolean warningLogged;

    public OsgiSafeStreamSerializer() {
    }

    public OsgiSafeStreamSerializer(OsgiClassLoaderRegistry registry) {
        this.registry = registry;
    }

    public void destroy() {
        if (this.registry != null) {
            this.registry.destroy();
        }
    }

    public int getTypeId() {
        return 1024;
    }

    public OsgiSafe read(ObjectDataInput in) throws IOException {
        ClassLoader bundleClassLoader;
        int bundleId = in.readInt();
        ClassLoader classLoader = bundleClassLoader = this.registry == null ? null : this.registry.getClassLoader(bundleId);
        if (bundleClassLoader == null) {
            log.debug("Could not find classloader for bundle {}. Attempting to deserialize with the current classloader", (Object)bundleId);
            bundleClassLoader = OsgiSafe.class.getClassLoader();
        }
        return new OsgiSafe<Object>(this.safeReadObject(in, bundleClassLoader, bundleId));
    }

    public void setRegistry(OsgiClassLoaderRegistry registry) {
        this.registry = registry;
    }

    public void setSerializationService(InternalSerializationService serializationService) {
        this.serializationService = serializationService;
    }

    public void write(ObjectDataOutput out, OsgiSafe object) throws IOException {
        Object value = ((OsgiSafe)Preconditions.checkNotNull((Object)object, (Object)"object")).getValue();
        out.writeInt(this.registry == null ? -1 : this.registry.getBundleId(value));
        out.writeObject(value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Object safeReadObject(ObjectDataInput in, ClassLoader classLoader, int bundleId) throws IOException {
        if (this.serializationService != null) {
            return this.serializationService.readObject((ObjectDataInput)new CustomClassLoaderObjectDataInput(classLoader, in));
        }
        if (!this.warningLogged) {
            this.warningLogged = true;
            log.warn("SerializationService is not set. Serialization and deserialization of classes provided by OSGI bundles will be slower.");
        }
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        try {
            Object object = in.readObject();
            Thread.currentThread().setContextClassLoader(originalClassLoader);
            return object;
        }
        catch (Throwable throwable) {
            try {
                Thread.currentThread().setContextClassLoader(originalClassLoader);
                throw throwable;
            }
            catch (RuntimeException e) {
                Throwable cause = Throwables.getRootCause((Throwable)e);
                if (cause instanceof ClassNotFoundException) {
                    log.info("ClassNotFoundException during deserialization of object from OSGI bundle {}: {}", (Object)(this.registry == null ? "unknown" : this.registry.getBundleName(bundleId)), (Object)cause.getMessage());
                    return null;
                }
                throw e;
            }
        }
    }
}

