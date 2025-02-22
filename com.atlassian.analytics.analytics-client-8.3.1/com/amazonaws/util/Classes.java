/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.jar.JarFile;

public enum Classes {


    public static Class<?> childClassOf(Class<?> parentClass, Object instance) {
        if (instance == null || instance == Object.class) {
            return null;
        }
        if (parentClass != null && parentClass.isInterface()) {
            return null;
        }
        Class<?> childClass = instance.getClass();
        Class<?> parent;
        while ((parent = childClass.getSuperclass()) != parentClass) {
            if (parent == null) {
                return null;
            }
            childClass = parent;
        }
        return childClass;
    }

    public static JarFile jarFileOf(Class<?> klass) {
        URL url = klass.getResource("/" + klass.getName().replace('.', '/') + ".class");
        if (url == null) {
            return null;
        }
        String s = url.getFile();
        int beginIndex = s.indexOf("file:") + "file:".length();
        int endIndex = s.indexOf(".jar!");
        if (endIndex == -1) {
            return null;
        }
        String f = s.substring(beginIndex, endIndex += ".jar".length());
        File file = new File(f);
        try {
            return file.exists() ? new JarFile(file) : null;
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}

