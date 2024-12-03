/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.core.util.CompositeClassLoader;

public class ClassLoaderReference
extends ClassLoader {
    private transient ClassLoader reference;

    public ClassLoaderReference(ClassLoader reference) {
        this.reference = reference;
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        return this.reference.loadClass(name);
    }

    public ClassLoader getReference() {
        return this.reference;
    }

    public void setReference(ClassLoader reference) {
        this.reference = reference;
    }

    private Object writeReplace() {
        return new Replacement();
    }

    static class Replacement {
        Replacement() {
        }

        private Object readResolve() {
            return new ClassLoaderReference(new CompositeClassLoader());
        }
    }
}

