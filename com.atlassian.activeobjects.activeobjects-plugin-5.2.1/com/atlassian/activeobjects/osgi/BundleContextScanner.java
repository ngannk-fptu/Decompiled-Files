/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Iterables
 *  org.osgi.framework.BundleContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.activeobjects.osgi;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.net.URL;
import java.util.Enumeration;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class BundleContextScanner {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    BundleContextScanner() {
    }

    <T> Iterable<T> findClasses(BundleContext bundleContext, String packageName, Function<String, T> f, Predicate<T> p) {
        Preconditions.checkNotNull((Object)bundleContext);
        Preconditions.checkNotNull((Object)packageName);
        Preconditions.checkNotNull(f);
        Preconditions.checkNotNull(p);
        return Iterables.filter(this.toIterable(this.getBundleEntries(bundleContext, packageName), f), p);
    }

    private Enumeration getBundleEntries(BundleContext bundleContext, String packageName) {
        this.log.debug("Scanning package '{}' of bundle {}", (Object)packageName, (Object)bundleContext.getBundle());
        return bundleContext.getBundle().findEntries(this.toFolder(packageName), "*.class", true);
    }

    private <T> Iterable<T> toIterable(Enumeration entries, Function<String, T> f) {
        ImmutableList.Builder classes = ImmutableList.builder();
        if (entries != null) {
            while (entries.hasMoreElements()) {
                String className = this.getClassName((URL)entries.nextElement());
                this.log.debug("Found class '{}'", (Object)className);
                classes.add(f.apply((Object)className));
            }
        }
        return classes.build();
    }

    private String getClassName(URL url) {
        return this.getClassName(url.getFile());
    }

    private String getClassName(String file) {
        String className = file.substring(1);
        className = className.substring(0, className.lastIndexOf(46));
        return className.replace('/', '.');
    }

    private String toFolder(String packageName) {
        return '/' + packageName.replace('.', '/');
    }
}

