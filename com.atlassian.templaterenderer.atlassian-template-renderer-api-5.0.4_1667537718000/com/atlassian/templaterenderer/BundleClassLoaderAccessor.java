/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.resource.AlternativeDirectoryResourceLoader
 *  com.atlassian.plugin.util.resource.AlternativeResourceLoader
 *  com.atlassian.plugin.util.resource.NoOpAlternativeResourceLoader
 *  org.apache.commons.collections.iterators.IteratorEnumeration
 *  org.osgi.framework.Bundle
 */
package com.atlassian.templaterenderer;

import com.atlassian.plugin.util.resource.AlternativeDirectoryResourceLoader;
import com.atlassian.plugin.util.resource.AlternativeResourceLoader;
import com.atlassian.plugin.util.resource.NoOpAlternativeResourceLoader;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;
import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.osgi.framework.Bundle;

public class BundleClassLoaderAccessor {
    public static ClassLoader getClassLoader(Bundle bundle) {
        return new BundleClassLoader(bundle, (AlternativeResourceLoader)new AlternativeDirectoryResourceLoader());
    }

    private static class BundleClassLoader
    extends ClassLoader {
        private final Bundle bundle;
        private final AlternativeResourceLoader altResourceLoader;

        public BundleClassLoader(Bundle bundle, AlternativeResourceLoader altResourceLoader) {
            super(null);
            Objects.requireNonNull(bundle, "The bundle must not be null");
            if (altResourceLoader == null) {
                altResourceLoader = new NoOpAlternativeResourceLoader();
            }
            this.altResourceLoader = altResourceLoader;
            this.bundle = bundle;
        }

        @Override
        public Class<?> findClass(String name) throws ClassNotFoundException {
            return this.bundle.loadClass(name);
        }

        @Override
        public Enumeration<URL> findResources(String name) throws IOException {
            URL resource;
            Enumeration e = this.bundle.getResources(name);
            if (e != null && !e.hasMoreElements() && (resource = this.findResource(name)) != null) {
                e = new IteratorEnumeration(Arrays.asList(resource).iterator());
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
    }
}

