/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.util;

import java.io.IOException;
import java.io.InputStream;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.Repository;

abstract class AbstractClassPathRepository
implements Repository {
    private final ClassPath classPath;

    AbstractClassPathRepository(ClassPath classPath) {
        this.classPath = classPath;
    }

    @Override
    public abstract void clear();

    @Override
    public abstract JavaClass findClass(String var1);

    @Override
    public ClassPath getClassPath() {
        return this.classPath;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public JavaClass loadClass(Class<?> clazz) throws ClassNotFoundException {
        String className = clazz.getName();
        JavaClass repositoryClass = this.findClass(className);
        if (repositoryClass != null) {
            return repositoryClass;
        }
        String name = className;
        int i = name.lastIndexOf(46);
        if (i > 0) {
            name = name.substring(i + 1);
        }
        try (InputStream clsStream = clazz.getResourceAsStream(name + ".class");){
            JavaClass javaClass = this.loadClass(clsStream, className);
            return javaClass;
        }
        catch (IOException e) {
            return null;
        }
    }

    private JavaClass loadClass(InputStream inputStream, String className) throws ClassNotFoundException {
        try {
            if (inputStream != null) {
                ClassParser parser = new ClassParser(inputStream, className);
                JavaClass clazz = parser.parse();
                this.storeClass(clazz);
                return clazz;
            }
        }
        catch (IOException e) {
            throw new ClassNotFoundException("Exception while looking for class " + className + ": " + e, e);
        }
        throw new ClassNotFoundException("ClassRepository could not load " + className);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public JavaClass loadClass(String className) throws ClassNotFoundException {
        if (className == null) throw new IllegalArgumentException("Invalid class name " + className);
        if (className.isEmpty()) {
            throw new IllegalArgumentException("Invalid class name " + className);
        }
        JavaClass clazz = this.findClass(className = Utility.pathToPackage(className));
        if (clazz != null) {
            return clazz;
        }
        try (InputStream inputStream = this.classPath.getInputStream(className);){
            JavaClass javaClass = this.loadClass(inputStream, className);
            return javaClass;
        }
        catch (IOException e) {
            throw new ClassNotFoundException("Exception while looking for class " + className + ": " + e, e);
        }
    }

    @Override
    public abstract void removeClass(JavaClass var1);

    @Override
    public abstract void storeClass(JavaClass var1);
}

