/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel;

import java.io.IOException;
import org.aspectj.apache.bcel.classfile.JavaClass;
import org.aspectj.apache.bcel.util.ClassPath;
import org.aspectj.apache.bcel.util.SyntheticRepository;

public abstract class Repository {
    private static org.aspectj.apache.bcel.util.Repository _repository = null;

    public static org.aspectj.apache.bcel.util.Repository getRepository() {
        if (_repository == null) {
            _repository = SyntheticRepository.getInstance();
        }
        return _repository;
    }

    public static void setRepository(org.aspectj.apache.bcel.util.Repository rep) {
        _repository = rep;
    }

    public static JavaClass lookupClass(String class_name) {
        try {
            JavaClass clazz = Repository.getRepository().findClass(class_name);
            if (clazz != null) {
                return clazz;
            }
            return Repository.getRepository().loadClass(class_name);
        }
        catch (ClassNotFoundException ex) {
            return null;
        }
    }

    public static ClassPath.ClassFile lookupClassFile(String class_name) {
        try {
            return ClassPath.getSystemClassPath().getClassFile(class_name);
        }
        catch (IOException e) {
            return null;
        }
    }

    public static void clearCache() {
        Repository.getRepository().clear();
    }

    public static JavaClass addClass(JavaClass clazz) {
        JavaClass old = Repository.getRepository().findClass(clazz.getClassName());
        Repository.getRepository().storeClass(clazz);
        return old;
    }

    public static void removeClass(String clazz) {
        Repository.getRepository().removeClass(Repository.getRepository().findClass(clazz));
    }

    public static boolean instanceOf(JavaClass clazz, JavaClass super_class) {
        return clazz.instanceOf(super_class);
    }

    public static boolean instanceOf(String clazz, String super_class) {
        return Repository.instanceOf(Repository.lookupClass(clazz), Repository.lookupClass(super_class));
    }

    public static boolean implementationOf(JavaClass clazz, JavaClass inter) {
        return clazz.implementationOf(inter);
    }

    public static boolean implementationOf(String clazz, String inter) {
        return Repository.implementationOf(Repository.lookupClass(clazz), Repository.lookupClass(inter));
    }
}

