/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.WeakHashMap;
import org.aspectj.apache.bcel.classfile.ClassParser;
import org.aspectj.apache.bcel.classfile.JavaClass;
import org.aspectj.apache.bcel.util.ClassPath;
import org.aspectj.apache.bcel.util.Repository;

public class SyntheticRepository
implements Repository {
    private static final String DEFAULT_PATH = ClassPath.getClassPath();
    private static HashMap<ClassPath, SyntheticRepository> _instances = new HashMap();
    private ClassPath _path = null;
    private WeakHashMap<String, JavaClass> _loadedClasses = new WeakHashMap();

    private SyntheticRepository(ClassPath path) {
        this._path = path;
    }

    public static SyntheticRepository getInstance() {
        return SyntheticRepository.getInstance(ClassPath.getSystemClassPath());
    }

    public static SyntheticRepository getInstance(ClassPath classPath) {
        SyntheticRepository rep = _instances.get(classPath);
        if (rep == null) {
            rep = new SyntheticRepository(classPath);
            _instances.put(classPath, rep);
        }
        return rep;
    }

    @Override
    public void storeClass(JavaClass clazz) {
        this._loadedClasses.put(clazz.getClassName(), clazz);
        clazz.setRepository(this);
    }

    @Override
    public void removeClass(JavaClass clazz) {
        this._loadedClasses.remove(clazz.getClassName());
    }

    @Override
    public JavaClass findClass(String className) {
        return this._loadedClasses.get(className);
    }

    @Override
    public JavaClass loadClass(String className) throws ClassNotFoundException {
        if (className == null || className.equals("")) {
            throw new IllegalArgumentException("Invalid class name " + className);
        }
        className = className.replace('/', '.');
        try {
            return this.loadClass(this._path.getInputStream(className), className);
        }
        catch (IOException e) {
            throw new ClassNotFoundException("Exception while looking for class " + className + ": " + e.toString());
        }
    }

    @Override
    public JavaClass loadClass(Class clazz) throws ClassNotFoundException {
        String className = clazz.getName();
        String name = className;
        int i = name.lastIndexOf(46);
        if (i > 0) {
            name = name.substring(i + 1);
        }
        return this.loadClass(clazz.getResourceAsStream(name + ".class"), className);
    }

    private JavaClass loadClass(InputStream is, String className) throws ClassNotFoundException {
        JavaClass clazz = this.findClass(className);
        if (clazz != null) {
            return clazz;
        }
        try {
            if (is != null) {
                ClassParser parser = new ClassParser(is, className);
                clazz = parser.parse();
                this.storeClass(clazz);
                return clazz;
            }
        }
        catch (IOException e) {
            throw new ClassNotFoundException("Exception while looking for class " + className + ": " + e.toString());
        }
        throw new ClassNotFoundException("SyntheticRepository could not load " + className);
    }

    @Override
    public void clear() {
        this._loadedClasses.clear();
    }
}

