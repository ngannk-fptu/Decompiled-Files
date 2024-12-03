/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel;

import java.io.IOException;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.SyntheticRepository;

public abstract class Repository {
    private static org.apache.bcel.util.Repository repository = SyntheticRepository.getInstance();

    public static JavaClass addClass(JavaClass clazz) {
        JavaClass old = repository.findClass(clazz.getClassName());
        repository.storeClass(clazz);
        return old;
    }

    public static void clearCache() {
        repository.clear();
    }

    public static JavaClass[] getInterfaces(JavaClass clazz) throws ClassNotFoundException {
        return clazz.getAllInterfaces();
    }

    public static JavaClass[] getInterfaces(String className) throws ClassNotFoundException {
        return Repository.getInterfaces(Repository.lookupClass(className));
    }

    public static org.apache.bcel.util.Repository getRepository() {
        return repository;
    }

    public static JavaClass[] getSuperClasses(JavaClass clazz) throws ClassNotFoundException {
        return clazz.getSuperClasses();
    }

    public static JavaClass[] getSuperClasses(String className) throws ClassNotFoundException {
        return Repository.getSuperClasses(Repository.lookupClass(className));
    }

    public static boolean implementationOf(JavaClass clazz, JavaClass inter) throws ClassNotFoundException {
        return clazz.implementationOf(inter);
    }

    public static boolean implementationOf(JavaClass clazz, String inter) throws ClassNotFoundException {
        return Repository.implementationOf(clazz, Repository.lookupClass(inter));
    }

    public static boolean implementationOf(String clazz, JavaClass inter) throws ClassNotFoundException {
        return Repository.implementationOf(Repository.lookupClass(clazz), inter);
    }

    public static boolean implementationOf(String clazz, String inter) throws ClassNotFoundException {
        return Repository.implementationOf(Repository.lookupClass(clazz), Repository.lookupClass(inter));
    }

    public static boolean instanceOf(JavaClass clazz, JavaClass superclass) throws ClassNotFoundException {
        return clazz.instanceOf(superclass);
    }

    public static boolean instanceOf(JavaClass clazz, String superclass) throws ClassNotFoundException {
        return Repository.instanceOf(clazz, Repository.lookupClass(superclass));
    }

    public static boolean instanceOf(String clazz, JavaClass superclass) throws ClassNotFoundException {
        return Repository.instanceOf(Repository.lookupClass(clazz), superclass);
    }

    public static boolean instanceOf(String clazz, String superclass) throws ClassNotFoundException {
        return Repository.instanceOf(Repository.lookupClass(clazz), Repository.lookupClass(superclass));
    }

    public static JavaClass lookupClass(Class<?> clazz) throws ClassNotFoundException {
        return repository.loadClass(clazz);
    }

    public static JavaClass lookupClass(String className) throws ClassNotFoundException {
        return repository.loadClass(className);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static ClassPath.ClassFile lookupClassFile(String className) {
        try (ClassPath path = repository.getClassPath();){
            ClassPath.ClassFile classFile = path == null ? null : path.getClassFile(className);
            return classFile;
        }
        catch (IOException e) {
            return null;
        }
    }

    public static void removeClass(JavaClass clazz) {
        repository.removeClass(clazz);
    }

    public static void removeClass(String clazz) {
        repository.removeClass(repository.findClass(clazz));
    }

    public static void setRepository(org.apache.bcel.util.Repository rep) {
        repository = rep;
    }
}

