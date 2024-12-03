/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.launch.Locator
 */
package org.apache.tools.ant.util;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.launch.Locator;
import org.apache.tools.ant.util.FileUtils;

public class LoaderUtils {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();

    public static void setContextClassLoader(ClassLoader loader) {
        Thread currentThread = Thread.currentThread();
        currentThread.setContextClassLoader(loader);
    }

    public static ClassLoader getContextClassLoader() {
        Thread currentThread = Thread.currentThread();
        return currentThread.getContextClassLoader();
    }

    public static boolean isContextLoaderAvailable() {
        return true;
    }

    private static File normalizeSource(File source) {
        if (source != null) {
            try {
                source = FILE_UTILS.normalize(source.getAbsolutePath());
            }
            catch (BuildException buildException) {
                // empty catch block
            }
        }
        return source;
    }

    public static File getClassSource(Class<?> c) {
        return LoaderUtils.normalizeSource(Locator.getClassSource(c));
    }

    public static File getResourceSource(ClassLoader c, String resource) {
        if (c == null) {
            c = LoaderUtils.class.getClassLoader();
        }
        return LoaderUtils.normalizeSource(Locator.getResourceSource((ClassLoader)c, (String)resource));
    }

    public static String classNameToResource(String className) {
        return className.replace('.', '/') + ".class";
    }

    public static boolean classExists(ClassLoader loader, String className) {
        return loader.getResource(LoaderUtils.classNameToResource(className)) != null;
    }
}

