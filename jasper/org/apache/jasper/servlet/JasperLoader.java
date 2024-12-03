/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jasper.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.PermissionCollection;
import org.apache.jasper.Constants;

public class JasperLoader
extends URLClassLoader {
    private final PermissionCollection permissionCollection;
    private final SecurityManager securityManager;

    public JasperLoader(URL[] urls, ClassLoader parent, PermissionCollection permissionCollection) {
        super(urls, parent);
        this.permissionCollection = permissionCollection;
        this.securityManager = System.getSecurityManager();
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return this.loadClass(name, false);
    }

    @Override
    public synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        int dot;
        Class<?> clazz = null;
        clazz = this.findLoadedClass(name);
        if (clazz != null) {
            if (resolve) {
                this.resolveClass(clazz);
            }
            return clazz;
        }
        if (this.securityManager != null && (dot = name.lastIndexOf(46)) >= 0) {
            try {
                if (!"org.apache.jasper.runtime".equalsIgnoreCase(name.substring(0, dot))) {
                    this.securityManager.checkPackageAccess(name.substring(0, dot));
                }
            }
            catch (SecurityException se) {
                String error = "Security Violation, attempt to use Restricted Class: " + name;
                se.printStackTrace();
                throw new ClassNotFoundException(error);
            }
        }
        if (!name.startsWith(Constants.JSP_PACKAGE_NAME + '.')) {
            clazz = this.getParent().loadClass(name);
            if (resolve) {
                this.resolveClass(clazz);
            }
            return clazz;
        }
        return this.findClass(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        URL url;
        InputStream is = this.getParent().getResourceAsStream(name);
        if (is == null && (url = this.findResource(name)) != null) {
            try {
                is = url.openStream();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return is;
    }

    @Override
    public final PermissionCollection getPermissions(CodeSource codeSource) {
        return this.permissionCollection;
    }
}

