/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.AbstractClassPathRepository;
import org.apache.bcel.util.ClassPath;

public class ClassPathRepository
extends AbstractClassPathRepository {
    private final Map<String, JavaClass> loadedClasses = new HashMap<String, JavaClass>();

    public ClassPathRepository(ClassPath classPath) {
        super(classPath);
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

