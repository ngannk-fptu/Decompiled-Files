/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.util;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.AbstractClassPathRepository;
import org.apache.bcel.util.ClassPath;

public class LruCacheClassPathRepository
extends AbstractClassPathRepository {
    private final LinkedHashMap<String, JavaClass> loadedClasses;

    public LruCacheClassPathRepository(ClassPath path, final int cacheSize) {
        super(path);
        if (cacheSize < 1) {
            throw new IllegalArgumentException("cacheSize must be a positive number.");
        }
        int initialCapacity = (int)(0.75 * (double)cacheSize);
        boolean accessOrder = true;
        this.loadedClasses = new LinkedHashMap<String, JavaClass>(initialCapacity, (float)cacheSize, true){
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, JavaClass> eldest) {
                return this.size() > cacheSize;
            }
        };
    }

    @Override
    public void clear() {
        this.loadedClasses.clear();
    }

    @Override
    public JavaClass findClass(String className) {
        return this.loadedClasses.get(className);
    }

    @Override
    public void removeClass(JavaClass javaClass) {
        this.loadedClasses.remove(javaClass.getClassName());
    }

    @Override
    public void storeClass(JavaClass javaClass) {
        this.loadedClasses.put(javaClass.getClassName(), javaClass);
        javaClass.setRepository(this);
    }
}

