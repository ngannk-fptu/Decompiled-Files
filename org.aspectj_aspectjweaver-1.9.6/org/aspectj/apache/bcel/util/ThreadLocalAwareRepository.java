/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.util;

import java.lang.ref.WeakReference;
import org.aspectj.apache.bcel.classfile.JavaClass;
import org.aspectj.apache.bcel.util.Repository;
import org.aspectj.apache.bcel.util.SyntheticRepository;

public class ThreadLocalAwareRepository
implements Repository {
    private static ThreadLocal<WeakReference<Repository>> threadLocal = new ThreadLocal();

    public static void setCurrentRepository(Repository repository) {
        threadLocal.set(new WeakReference<Repository>(repository));
    }

    private static Repository currentRepository() {
        WeakReference<Repository> ref = threadLocal.get();
        Repository repo = null;
        if (ref != null) {
            repo = (Repository)ref.get();
        }
        if (repo == null) {
            repo = SyntheticRepository.getInstance();
        }
        return repo;
    }

    @Override
    public void storeClass(JavaClass clazz) {
        ThreadLocalAwareRepository.currentRepository().storeClass(clazz);
    }

    @Override
    public void removeClass(JavaClass clazz) {
        ThreadLocalAwareRepository.currentRepository().removeClass(clazz);
    }

    @Override
    public JavaClass findClass(String className) {
        return ThreadLocalAwareRepository.currentRepository().findClass(className);
    }

    @Override
    public JavaClass loadClass(String className) throws ClassNotFoundException {
        return ThreadLocalAwareRepository.currentRepository().loadClass(className);
    }

    @Override
    public JavaClass loadClass(Class clazz) throws ClassNotFoundException {
        return ThreadLocalAwareRepository.currentRepository().loadClass(clazz);
    }

    @Override
    public void clear() {
        ThreadLocalAwareRepository.currentRepository().clear();
    }
}

