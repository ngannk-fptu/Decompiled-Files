/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.core.JVM;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CompositeClassLoader
extends ClassLoader {
    private final ReferenceQueue queue = new ReferenceQueue();
    private final List classLoaders = new ArrayList();

    public CompositeClassLoader() {
        this.addInternal(Object.class.getClassLoader());
        this.addInternal(this.getClass().getClassLoader());
    }

    public synchronized void add(ClassLoader classLoader) {
        this.cleanup();
        if (classLoader != null) {
            this.addInternal(classLoader);
        }
    }

    private void addInternal(ClassLoader classLoader) {
        WeakReference refClassLoader = null;
        Iterator iterator = this.classLoaders.iterator();
        while (iterator.hasNext()) {
            WeakReference ref = (WeakReference)iterator.next();
            ClassLoader cl = (ClassLoader)ref.get();
            if (cl == null) {
                iterator.remove();
                continue;
            }
            if (cl != classLoader) continue;
            iterator.remove();
            refClassLoader = ref;
        }
        this.classLoaders.add(0, refClassLoader != null ? refClassLoader : new WeakReference(classLoader, this.queue));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Class loadClass(String name) throws ClassNotFoundException {
        ArrayList copy = new ArrayList(this.classLoaders.size()){

            public boolean addAll(Collection c) {
                boolean result = false;
                Iterator iter = c.iterator();
                while (iter.hasNext()) {
                    result |= this.add(iter.next());
                }
                return result;
            }

            public boolean add(Object ref) {
                Object classLoader = ((WeakReference)ref).get();
                if (classLoader != null) {
                    return super.add(classLoader);
                }
                return false;
            }
        };
        CompositeClassLoader compositeClassLoader = this;
        synchronized (compositeClassLoader) {
            this.cleanup();
            copy.addAll(this.classLoaders);
        }
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Iterator iterator = copy.iterator();
        while (iterator.hasNext()) {
            ClassLoader classLoader = (ClassLoader)iterator.next();
            if (classLoader == contextClassLoader) {
                contextClassLoader = null;
            }
            try {
                return classLoader.loadClass(name);
            }
            catch (ClassNotFoundException classNotFoundException) {
            }
        }
        if (contextClassLoader != null) {
            return contextClassLoader.loadClass(name);
        }
        throw new ClassNotFoundException(name);
    }

    private void cleanup() {
        WeakReference ref;
        while ((ref = (WeakReference)this.queue.poll()) != null) {
            this.classLoaders.remove(ref);
        }
    }

    static {
        if (JVM.isVersion(7)) {
            try {
                Method m = ClassLoader.class.getDeclaredMethod("registerAsParallelCapable", null);
                if (!m.isAccessible()) {
                    m.setAccessible(true);
                }
                m.invoke(null, (Object[])null);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }
}

