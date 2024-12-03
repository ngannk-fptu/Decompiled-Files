/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.util;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassPath;

public interface Repository {
    public void clear();

    public JavaClass findClass(String var1);

    public ClassPath getClassPath();

    public JavaClass loadClass(Class<?> var1) throws ClassNotFoundException;

    public JavaClass loadClass(String var1) throws ClassNotFoundException;

    public void removeClass(JavaClass var1);

    public void storeClass(JavaClass var1);
}

