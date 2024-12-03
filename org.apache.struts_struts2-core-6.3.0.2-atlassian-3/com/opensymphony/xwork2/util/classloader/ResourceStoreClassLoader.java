/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util.classloader;

import com.opensymphony.xwork2.util.classloader.ResourceStore;

public final class ResourceStoreClassLoader
extends ClassLoader {
    private final ResourceStore[] stores;

    public ResourceStoreClassLoader(ClassLoader pParent, ResourceStore[] pStores) {
        super(pParent);
        this.stores = new ResourceStore[pStores.length];
        System.arraycopy(pStores, 0, this.stores, 0, this.stores.length);
    }

    private Class fastFindClass(String name) {
        if (this.stores != null) {
            String fileName = name.replace('.', '/') + ".class";
            for (ResourceStore store : this.stores) {
                byte[] clazzBytes = store.read(fileName);
                if (clazzBytes == null) continue;
                this.definePackage(name);
                return this.defineClass(name, clazzBytes, 0, clazzBytes.length);
            }
        }
        return null;
    }

    protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = this.findLoadedClass(name);
        if (clazz == null && (clazz = this.fastFindClass(name)) == null) {
            ClassLoader parent = this.getParent();
            if (parent != null) {
                clazz = parent.loadClass(name);
            } else {
                throw new ClassNotFoundException(name);
            }
        }
        if (resolve) {
            this.resolveClass(clazz);
        }
        return clazz;
    }

    protected Class findClass(String name) throws ClassNotFoundException {
        Class clazz = this.fastFindClass(name);
        if (clazz == null) {
            throw new ClassNotFoundException(name);
        }
        return clazz;
    }

    protected void definePackage(String className) {
        int classIndex = className.lastIndexOf(46);
        if (classIndex == -1) {
            return;
        }
        String packageName = className.substring(0, classIndex);
        if (this.getPackage(packageName) != null) {
            return;
        }
        this.definePackage(packageName, null, null, null, null, null, null, null);
    }
}

