/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Proxy;
import org.apache.catalina.tribes.util.StringManager;

public final class ReplicationStream
extends ObjectInputStream {
    static final StringManager sm = StringManager.getManager(ReplicationStream.class);
    private ClassLoader[] classLoaders = null;

    public ReplicationStream(InputStream stream, ClassLoader[] classLoaders) throws IOException {
        super(stream);
        this.classLoaders = classLoaders;
    }

    @Override
    public Class<?> resolveClass(ObjectStreamClass classDesc) throws ClassNotFoundException, IOException {
        String name = classDesc.getName();
        try {
            return this.resolveClass(name);
        }
        catch (ClassNotFoundException e) {
            return super.resolveClass(classDesc);
        }
    }

    public Class<?> resolveClass(String name) throws ClassNotFoundException {
        boolean tryRepFirst = name.startsWith("org.apache.catalina.tribes");
        try {
            if (tryRepFirst) {
                return this.findReplicationClass(name);
            }
            return this.findExternalClass(name);
        }
        catch (Exception x) {
            if (tryRepFirst) {
                return this.findExternalClass(name);
            }
            return this.findReplicationClass(name);
        }
    }

    @Override
    protected Class<?> resolveProxyClass(String[] interfaces) throws IOException, ClassNotFoundException {
        ClassLoader latestLoader = this.classLoaders.length > 0 ? this.classLoaders[0] : null;
        ClassLoader nonPublicLoader = null;
        boolean hasNonPublicInterface = false;
        Class[] classObjs = new Class[interfaces.length];
        for (int i = 0; i < interfaces.length; ++i) {
            Class<?> cl = this.resolveClass(interfaces[i]);
            if (latestLoader == null) {
                latestLoader = cl.getClassLoader();
            }
            if ((cl.getModifiers() & 1) == 0) {
                if (hasNonPublicInterface) {
                    if (nonPublicLoader != cl.getClassLoader()) {
                        throw new IllegalAccessError(sm.getString("replicationStream.conflict"));
                    }
                } else {
                    nonPublicLoader = cl.getClassLoader();
                    hasNonPublicInterface = true;
                }
            }
            classObjs[i] = cl;
        }
        try {
            Class<?> proxyClass = Proxy.getProxyClass(hasNonPublicInterface ? nonPublicLoader : latestLoader, classObjs);
            return proxyClass;
        }
        catch (IllegalArgumentException e) {
            throw new ClassNotFoundException(null, e);
        }
    }

    public Class<?> findReplicationClass(String name) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(name, false, this.getClass().getClassLoader());
        return clazz;
    }

    public Class<?> findExternalClass(String name) throws ClassNotFoundException {
        ClassNotFoundException cnfe = null;
        for (ClassLoader classLoader : this.classLoaders) {
            try {
                Class<?> clazz = Class.forName(name, false, classLoader);
                return clazz;
            }
            catch (ClassNotFoundException x) {
                cnfe = x;
            }
        }
        if (cnfe != null) {
            throw cnfe;
        }
        throw new ClassNotFoundException(name);
    }

    @Override
    public void close() throws IOException {
        this.classLoaders = null;
        super.close();
    }
}

