/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.aspectj.util.LangUtil;

public class UtilClassLoader
extends URLClassLoader {
    List<File> dirs;
    private URL[] urlsForDebugString;

    public UtilClassLoader(URL[] urls, File[] dirs) {
        super(urls);
        LangUtil.throwIaxIfNotAssignable(dirs, File.class, "dirs");
        this.urlsForDebugString = urls;
        ArrayList<File> dcopy = new ArrayList<File>();
        if (!LangUtil.isEmpty(dirs)) {
            dcopy.addAll(Arrays.asList(dirs));
        }
        this.dirs = Collections.unmodifiableList(dcopy);
    }

    @Override
    public URL getResource(String name) {
        return ClassLoader.getSystemResource(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return ClassLoader.getSystemResourceAsStream(name);
    }

    @Override
    public synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        byte[] data;
        ClassNotFoundException thrown = null;
        Class<?> result = this.findLoadedClass(name);
        if (null != result) {
            resolve = false;
        } else {
            try {
                result = this.findSystemClass(name);
            }
            catch (ClassNotFoundException e) {
                thrown = e;
            }
        }
        if (null == result) {
            try {
                result = super.loadClass(name, resolve);
            }
            catch (ClassNotFoundException e) {
                thrown = e;
            }
            if (null != result) {
                return result;
            }
        }
        if (null == result && (data = this.readClass(name)) != null) {
            result = this.defineClass(name, data, 0, data.length);
        }
        if (null == result) {
            throw null != thrown ? thrown : new ClassNotFoundException(name);
        }
        if (resolve) {
            this.resolveClass(result);
        }
        return result;
    }

    private byte[] readClass(String className) throws ClassNotFoundException {
        String fileName = className.replace('.', '/') + ".class";
        Iterator<File> iter = this.dirs.iterator();
        while (iter.hasNext()) {
            File file = new File(iter.next(), fileName);
            if (!file.canRead()) continue;
            return this.getClassData(file);
        }
        return null;
    }

    private byte[] getClassData(File f) {
        try {
            int n;
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[4096];
            while ((n = stream.read(b)) != -1) {
                out.write(b, 0, n);
            }
            stream.close();
            out.close();
            return out.toByteArray();
        }
        catch (IOException iOException) {
            return null;
        }
    }

    public String toString() {
        return "UtilClassLoader(urls=" + Arrays.asList(this.urlsForDebugString) + ", dirs=" + this.dirs + ")";
    }
}

