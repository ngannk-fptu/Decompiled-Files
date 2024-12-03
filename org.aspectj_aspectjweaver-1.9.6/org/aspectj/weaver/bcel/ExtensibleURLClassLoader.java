/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import org.aspectj.util.FileUtil;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.bcel.ClassPathManager;

public abstract class ExtensibleURLClassLoader
extends URLClassLoader {
    private ClassPathManager classPath;

    public ExtensibleURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        try {
            this.classPath = new ClassPathManager(FileUtil.makeClasspath(urls), null);
        }
        catch (ExceptionInInitializerError ex) {
            ex.printStackTrace(System.out);
            throw ex;
        }
    }

    @Override
    protected void addURL(URL url) {
        super.addURL(url);
        this.classPath.addPath(url.getPath(), null);
    }

    protected Class findClass(String name) throws ClassNotFoundException {
        try {
            byte[] bytes = this.getBytes(name);
            if (bytes != null) {
                return this.defineClass(name, bytes);
            }
            throw new ClassNotFoundException(name);
        }
        catch (IOException ex) {
            throw new ClassNotFoundException(name);
        }
    }

    protected Class defineClass(String name, byte[] b, CodeSource cs) throws IOException {
        return this.defineClass(name, b, 0, b.length, cs);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected byte[] getBytes(String name) throws IOException {
        byte[] b = null;
        UnresolvedType unresolvedType = null;
        try {
            unresolvedType = UnresolvedType.forName(name);
        }
        catch (BCException bce) {
            if (bce.getMessage().indexOf("nameToSignature") == -1) {
                bce.printStackTrace(System.err);
            }
            return null;
        }
        ClassPathManager.ClassFile classFile = this.classPath.find(unresolvedType);
        if (classFile != null) {
            try {
                b = FileUtil.readAsByteArray(classFile.getInputStream());
            }
            finally {
                classFile.close();
            }
        }
        return b;
    }

    private Class defineClass(String name, byte[] bytes) throws IOException {
        Package pakkage;
        String packageName = this.getPackageName(name);
        if (packageName != null && (pakkage = this.getPackage(packageName)) == null) {
            this.definePackage(packageName, null, null, null, null, null, null, null);
        }
        return this.defineClass(name, bytes, null);
    }

    private String getPackageName(String className) {
        int offset = className.lastIndexOf(46);
        return offset == -1 ? null : className.substring(0, offset);
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.classPath.closeArchives();
    }
}

