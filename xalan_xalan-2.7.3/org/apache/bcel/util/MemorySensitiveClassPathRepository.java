/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.util;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.AbstractClassPathRepository;
import org.apache.bcel.util.ClassPath;

public class MemorySensitiveClassPathRepository
extends AbstractClassPathRepository {
    private final Map<String, SoftReference<JavaClass>> loadedClasses = new HashMap<String, SoftReference<JavaClass>>();

    public MemorySensitiveClassPathRepository(ClassPath path) {
        super(path);
    }

    @Override
    public void clear() {
        this.loadedClasses.clear();
    }

    @Override
    public JavaClass findClass(String className) {
        SoftReference<JavaClass> ref = this.loadedClasses.get(className);
        return ref == null ? null : ref.get();
    }

    @Override
    public void removeClass(JavaClass clazz) {
        this.loadedClasses.remove(clazz.getClassName());
    }

    @Override
    public void storeClass(JavaClass clazz) {
        this.loadedClasses.put(clazz.getClassName(), new SoftReference<JavaClass>(clazz));
        clazz.setRepository(this);
    }
}

