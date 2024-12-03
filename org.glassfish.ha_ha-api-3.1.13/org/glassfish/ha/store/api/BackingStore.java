/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import org.glassfish.ha.store.api.BackingStoreConfiguration;
import org.glassfish.ha.store.api.BackingStoreException;
import org.glassfish.ha.store.api.BackingStoreFactory;
import org.glassfish.ha.store.spi.ObjectInputOutputStreamFactory;
import org.glassfish.ha.store.spi.ObjectInputOutputStreamFactoryRegistry;
import org.glassfish.ha.store.spi.ObjectInputStreamWithLoader;

public abstract class BackingStore<K extends Serializable, V extends Serializable> {
    BackingStoreConfiguration<K, V> conf;

    protected void initialize(BackingStoreConfiguration<K, V> conf) throws BackingStoreException {
        this.conf = conf;
    }

    protected BackingStoreConfiguration<K, V> getBackingStoreConfiguration() {
        return this.conf;
    }

    public abstract BackingStoreFactory getBackingStoreFactory();

    public abstract V load(K var1, String var2) throws BackingStoreException;

    public abstract String save(K var1, V var2, boolean var3) throws BackingStoreException;

    public abstract void remove(K var1) throws BackingStoreException;

    public void updateTimestamp(K key, long time) throws BackingStoreException {
    }

    public int removeExpired(long idleForMillis) throws BackingStoreException {
        return 0;
    }

    public String updateTimestamp(K key, String version, Long accessTime) throws BackingStoreException {
        return "";
    }

    public int removeExpired() throws BackingStoreException {
        return 0;
    }

    public abstract int size() throws BackingStoreException;

    public void close() throws BackingStoreException {
    }

    public void destroy() throws BackingStoreException {
    }

    protected ObjectOutputStream createObjectOutputStream(OutputStream os) throws IOException {
        ObjectInputOutputStreamFactory oosf = ObjectInputOutputStreamFactoryRegistry.getObjectInputOutputStreamFactory();
        return oosf == null ? new ObjectOutputStream(os) : oosf.createObjectOutputStream(os);
    }

    protected ObjectInputStream createObjectInputStream(InputStream is) throws IOException {
        return new ObjectInputStreamWithLoader(is, this.conf.getValueClazz().getClassLoader());
    }
}

