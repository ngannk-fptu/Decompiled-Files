/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.resource.AlternativeResourceLoader
 *  com.atlassian.plugin.util.resource.NoOpAlternativeResourceLoader
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterators
 *  org.osgi.framework.Bundle
 */
package com.atlassian.plugin.osgi.util;

import com.atlassian.plugin.util.resource.AlternativeResourceLoader;
import com.atlassian.plugin.util.resource.NoOpAlternativeResourceLoader;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import org.osgi.framework.Bundle;

public class BundleClassLoaderAccessor {
    public static ClassLoader getClassLoader(Bundle bundle, AlternativeResourceLoader alternativeResourceLoader) {
        return new BundleClassLoader(bundle, alternativeResourceLoader);
    }

    public static <T> Class<T> loadClass(Bundle bundle, String name) throws ClassNotFoundException {
        Class loadedClass = ((Bundle)Preconditions.checkNotNull((Object)bundle, (Object)"The bundle is required")).loadClass(name);
        return loadedClass;
    }

    private static class BundleClassLoader
    extends ClassLoader {
        private final Bundle bundle;
        private final AlternativeResourceLoader altResourceLoader;

        public BundleClassLoader(Bundle bundle, AlternativeResourceLoader altResourceLoader) {
            super(null);
            this.bundle = (Bundle)Preconditions.checkNotNull((Object)bundle, (Object)"The bundle must not be null");
            this.altResourceLoader = altResourceLoader == null ? new NoOpAlternativeResourceLoader() : altResourceLoader;
        }

        @Override
        public Class<?> findClass(String name) throws ClassNotFoundException {
            return this.bundle.loadClass(name);
        }

        @Override
        public Enumeration<URL> findResources(String name) throws IOException {
            URL resource;
            Enumeration e = this.bundle.getResources(name);
            if (e == null) {
                e = Iterators.asEnumeration(Collections.emptyList().iterator());
            } else if (!e.hasMoreElements() && (resource = this.findResource(name)) != null) {
                e = Iterators.asEnumeration(Collections.singleton(resource).iterator());
            }
            return e;
        }

        @Override
        public URL findResource(String name) {
            URL url = this.altResourceLoader.getResource(name);
            if (url == null) {
                url = this.bundle.getResource(name);
            }
            return url;
        }

        public String toString() {
            String sym = this.bundle.getSymbolicName();
            return "BundleClassLoader@" + Integer.toHexString(System.identityHashCode(this)) + "[bundle=" + (sym != null ? sym : "") + " [" + this.bundle.getBundleId() + "]]";
        }
    }
}

