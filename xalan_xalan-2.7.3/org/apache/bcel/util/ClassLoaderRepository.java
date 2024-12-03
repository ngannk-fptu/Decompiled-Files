/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.Repository;

public class ClassLoaderRepository
implements Repository {
    private final ClassLoader loader;
    private final Map<String, JavaClass> loadedClasses = new HashMap<String, JavaClass>();

    public ClassLoaderRepository(ClassLoader loader) {
        this.loader = loader;
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
    public ClassPath getClassPath() {
        return null;
    }

    @Override
    public JavaClass loadClass(Class<?> clazz) throws ClassNotFoundException {
        return this.loadClass(clazz.getName());
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public JavaClass loadClass(String className) throws ClassNotFoundException {
        String classFile = Utility.packageToPath(className);
        JavaClass RC = this.findClass(className);
        if (RC != null) {
            return RC;
        }
        try (InputStream is = this.loader.getResourceAsStream(classFile + ".class");){
            if (is == null) {
                throw new ClassNotFoundException(className + " not found.");
            }
            ClassParser parser = new ClassParser(is, className);
            RC = parser.parse();
            this.storeClass(RC);
            JavaClass javaClass = RC;
            return javaClass;
        }
        catch (IOException e) {
            throw new ClassNotFoundException(className + " not found: " + e, e);
        }
    }

    @Override
    public void removeClass(JavaClass clazz) {
        this.loadedClasses.remove(clazz.getClassName());
    }

    @Override
    public void storeClass(JavaClass clazz) {
        this.loadedClasses.put(clazz.getClassName(), clazz);
        clazz.setRepository(this);
    }
}

