/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleReference
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.util;

import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import org.apache.commons.logging.Log;
import org.eclipse.gemini.blueprint.util.DebugUtils;
import org.eclipse.gemini.blueprint.util.LogUtils;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;
import org.springframework.util.Assert;

public class BundleDelegatingClassLoader
extends ClassLoader
implements BundleReference {
    private static final Enumeration<URL> EMPTY_RESOURCES = new Enumeration<URL>(){

        @Override
        public boolean hasMoreElements() {
            return false;
        }

        @Override
        public URL nextElement() {
            throw new NoSuchElementException();
        }
    };
    private static final Log log = LogUtils.createLogger(BundleDelegatingClassLoader.class);
    private final ClassLoader bridge;
    private final Bundle backingBundle;

    public static BundleDelegatingClassLoader createBundleClassLoaderFor(Bundle aBundle) {
        return BundleDelegatingClassLoader.createBundleClassLoaderFor(aBundle, null);
    }

    public static BundleDelegatingClassLoader createBundleClassLoaderFor(final Bundle bundle, final ClassLoader bridge) {
        return AccessController.doPrivileged(new PrivilegedAction<BundleDelegatingClassLoader>(){

            @Override
            public BundleDelegatingClassLoader run() {
                return new BundleDelegatingClassLoader(bundle, bridge);
            }
        });
    }

    protected BundleDelegatingClassLoader(Bundle bundle, ClassLoader bridgeLoader) {
        super(null);
        Assert.notNull((Object)bundle, (String)"bundle should be non-null");
        this.backingBundle = bundle;
        this.bridge = bridgeLoader;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            return this.backingBundle.loadClass(name);
        }
        catch (ClassNotFoundException cnfe) {
            DebugUtils.debugClassLoading(this.backingBundle, name, null);
            throw new ClassNotFoundException(name + " not found from bundle [" + this.backingBundle.getSymbolicName() + "]", cnfe);
        }
        catch (NoClassDefFoundError ncdfe) {
            String cname = ncdfe.getMessage().replace('/', '.');
            DebugUtils.debugClassLoading(this.backingBundle, cname, name);
            NoClassDefFoundError e = new NoClassDefFoundError(name + " not found from bundle [" + OsgiStringUtils.nullSafeNameAndSymName(this.backingBundle) + "]");
            e.initCause(ncdfe);
            throw e;
        }
    }

    @Override
    protected URL findResource(String name) {
        boolean trace = log.isTraceEnabled();
        if (trace) {
            log.trace((Object)("Looking for resource " + name));
        }
        URL url = this.backingBundle.getResource(name);
        if (trace && url != null) {
            log.trace((Object)("Found resource " + name + " at " + url));
        }
        return url;
    }

    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        boolean trace = log.isTraceEnabled();
        if (trace) {
            log.trace((Object)("Looking for resources " + name));
        }
        Enumeration enm = this.backingBundle.getResources(name);
        if (trace && enm != null && enm.hasMoreElements()) {
            log.trace((Object)("Found resource " + name + " at " + this.backingBundle.getLocation()));
        }
        return enm;
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        Enumeration<URL> resources = this.backingBundle.getResources(name);
        if (this.bridge != null) {
            Enumeration<URL> bridgeResources = this.bridge.getResources(name);
            if (resources == null) {
                resources = bridgeResources;
            } else if (bridgeResources != null) {
                resources = new CombinedEnumeration<URL>(resources, bridgeResources);
            }
        }
        return resources != null ? resources : EMPTY_RESOURCES;
    }

    @Override
    public URL getResource(String name) {
        URL resource = this.findResource(name);
        if (this.bridge != null && resource == null) {
            resource = this.bridge.getResource(name);
        }
        return resource;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = null;
        try {
            clazz = this.findClass(name);
        }
        catch (ClassNotFoundException cnfe) {
            if (this.bridge != null) {
                clazz = this.bridge.loadClass(name);
            }
            throw cnfe;
        }
        if (resolve) {
            this.resolveClass(clazz);
        }
        return clazz;
    }

    public String toString() {
        return "BundleDelegatingClassLoader for [" + OsgiStringUtils.nullSafeNameAndSymName(this.backingBundle) + "]";
    }

    public Bundle getBundle() {
        return this.backingBundle;
    }

    private static class CombinedEnumeration<T>
    implements Enumeration<T> {
        private final Enumeration<T> e1;
        private final Enumeration<T> e2;

        public CombinedEnumeration(Enumeration<T> e1, Enumeration<T> e2) {
            this.e1 = e1;
            this.e2 = e2;
        }

        @Override
        public boolean hasMoreElements() {
            return this.e1.hasMoreElements() || this.e2.hasMoreElements();
        }

        @Override
        public T nextElement() {
            if (this.e1.hasMoreElements()) {
                return this.e1.nextElement();
            }
            if (this.e2.hasMoreElements()) {
                return this.e2.nextElement();
            }
            throw new NoSuchElementException();
        }
    }
}

