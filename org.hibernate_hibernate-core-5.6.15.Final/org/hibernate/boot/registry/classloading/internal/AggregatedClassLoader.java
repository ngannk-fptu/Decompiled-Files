/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.registry.classloading.internal;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import org.hibernate.boot.registry.classloading.internal.TcclLookupPrecedence;

public class AggregatedClassLoader
extends ClassLoader {
    private final ClassLoader[] individualClassLoaders;
    private final TcclLookupPrecedence tcclLookupPrecedence;

    public AggregatedClassLoader(LinkedHashSet<ClassLoader> orderedClassLoaderSet, TcclLookupPrecedence precedence) {
        super(null);
        this.individualClassLoaders = orderedClassLoaderSet.toArray(new ClassLoader[orderedClassLoaderSet.size()]);
        this.tcclLookupPrecedence = precedence;
    }

    Iterator<ClassLoader> newClassLoaderIterator() {
        ClassLoader threadClassLoader = AggregatedClassLoader.locateTCCL();
        if (this.tcclLookupPrecedence == TcclLookupPrecedence.NEVER || threadClassLoader == null) {
            return this.newTcclNeverIterator();
        }
        if (this.tcclLookupPrecedence == TcclLookupPrecedence.AFTER) {
            return this.newTcclAfterIterator(threadClassLoader);
        }
        if (this.tcclLookupPrecedence == TcclLookupPrecedence.BEFORE) {
            return this.newTcclBeforeIterator(threadClassLoader);
        }
        throw new RuntimeException("Unknown precedence: " + (Object)((Object)this.tcclLookupPrecedence));
    }

    private Iterator<ClassLoader> newTcclBeforeIterator(final ClassLoader threadContextClassLoader) {
        final ClassLoader systemClassLoader = AggregatedClassLoader.locateSystemClassLoader();
        return new Iterator<ClassLoader>(){
            private int currentIndex = 0;
            private boolean tcCLReturned = false;
            private boolean sysCLReturned = false;

            @Override
            public boolean hasNext() {
                if (!this.tcCLReturned) {
                    return true;
                }
                if (this.currentIndex < AggregatedClassLoader.this.individualClassLoaders.length) {
                    return true;
                }
                return !this.sysCLReturned && systemClassLoader != null;
            }

            @Override
            public ClassLoader next() {
                if (!this.tcCLReturned) {
                    this.tcCLReturned = true;
                    return threadContextClassLoader;
                }
                if (this.currentIndex < AggregatedClassLoader.this.individualClassLoaders.length) {
                    ++this.currentIndex;
                    return AggregatedClassLoader.this.individualClassLoaders[this.currentIndex - 1];
                }
                if (!this.sysCLReturned && systemClassLoader != null) {
                    this.sysCLReturned = true;
                    return systemClassLoader;
                }
                throw new IllegalStateException("No more item");
            }
        };
    }

    private Iterator<ClassLoader> newTcclAfterIterator(final ClassLoader threadContextClassLoader) {
        final ClassLoader systemClassLoader = AggregatedClassLoader.locateSystemClassLoader();
        return new Iterator<ClassLoader>(){
            private int currentIndex = 0;
            private boolean tcCLReturned = false;
            private boolean sysCLReturned = false;

            @Override
            public boolean hasNext() {
                if (this.currentIndex < AggregatedClassLoader.this.individualClassLoaders.length) {
                    return true;
                }
                if (!this.tcCLReturned) {
                    return true;
                }
                return !this.sysCLReturned && systemClassLoader != null;
            }

            @Override
            public ClassLoader next() {
                if (this.currentIndex < AggregatedClassLoader.this.individualClassLoaders.length) {
                    ++this.currentIndex;
                    return AggregatedClassLoader.this.individualClassLoaders[this.currentIndex - 1];
                }
                if (!this.tcCLReturned) {
                    this.tcCLReturned = true;
                    return threadContextClassLoader;
                }
                if (!this.sysCLReturned && systemClassLoader != null) {
                    this.sysCLReturned = true;
                    return systemClassLoader;
                }
                throw new IllegalStateException("No more item");
            }
        };
    }

    private Iterator<ClassLoader> newTcclNeverIterator() {
        final ClassLoader systemClassLoader = AggregatedClassLoader.locateSystemClassLoader();
        return new Iterator<ClassLoader>(){
            private int currentIndex = 0;
            private boolean sysCLReturned = false;

            @Override
            public boolean hasNext() {
                if (this.currentIndex < AggregatedClassLoader.this.individualClassLoaders.length) {
                    return true;
                }
                return !this.sysCLReturned && systemClassLoader != null;
            }

            @Override
            public ClassLoader next() {
                if (this.currentIndex < AggregatedClassLoader.this.individualClassLoaders.length) {
                    ++this.currentIndex;
                    return AggregatedClassLoader.this.individualClassLoaders[this.currentIndex - 1];
                }
                if (!this.sysCLReturned && systemClassLoader != null) {
                    this.sysCLReturned = true;
                    return systemClassLoader;
                }
                throw new IllegalStateException("No more item");
            }
        };
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        final LinkedHashSet<URL> resourceUrls = new LinkedHashSet<URL>();
        Iterator<ClassLoader> clIterator = this.newClassLoaderIterator();
        while (clIterator.hasNext()) {
            ClassLoader classLoader = clIterator.next();
            Enumeration<URL> urls = classLoader.getResources(name);
            while (urls.hasMoreElements()) {
                resourceUrls.add(urls.nextElement());
            }
        }
        return new Enumeration<URL>(){
            final Iterator<URL> resourceUrlIterator;
            {
                this.resourceUrlIterator = resourceUrls.iterator();
            }

            @Override
            public boolean hasMoreElements() {
                return this.resourceUrlIterator.hasNext();
            }

            @Override
            public URL nextElement() {
                return this.resourceUrlIterator.next();
            }
        };
    }

    @Override
    protected URL findResource(String name) {
        Iterator<ClassLoader> clIterator = this.newClassLoaderIterator();
        while (clIterator.hasNext()) {
            ClassLoader classLoader = clIterator.next();
            URL resource = classLoader.getResource(name);
            if (resource == null) continue;
            return resource;
        }
        return super.findResource(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Iterator<ClassLoader> clIterator = this.newClassLoaderIterator();
        while (clIterator.hasNext()) {
            ClassLoader classLoader = clIterator.next();
            try {
                return classLoader.loadClass(name);
            }
            catch (Exception exception) {
            }
            catch (LinkageError linkageError) {
            }
        }
        throw new ClassNotFoundException("Could not load requested class : " + name);
    }

    private static ClassLoader locateSystemClassLoader() {
        try {
            return ClassLoader.getSystemClassLoader();
        }
        catch (Exception e) {
            return null;
        }
    }

    private static ClassLoader locateTCCL() {
        try {
            return Thread.currentThread().getContextClassLoader();
        }
        catch (Exception e) {
            return null;
        }
    }
}

