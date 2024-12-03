/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.util;

import org.aspectj.apache.bcel.classfile.JavaClass;

public interface Repository {
    public void storeClass(JavaClass var1);

    public void removeClass(JavaClass var1);

    public JavaClass findClass(String var1);

    public JavaClass loadClass(String var1) throws ClassNotFoundException;

    public JavaClass loadClass(Class var1) throws ClassNotFoundException;

    public void clear();
}

