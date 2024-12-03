/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.dtm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

final class SecuritySupport {
    static ClassLoader getContextClassLoader() {
        return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                ClassLoader cl = null;
                try {
                    cl = Thread.currentThread().getContextClassLoader();
                }
                catch (SecurityException securityException) {
                    // empty catch block
                }
                return cl;
            }
        });
    }

    static ClassLoader getSystemClassLoader() {
        return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                ClassLoader cl = null;
                try {
                    cl = ClassLoader.getSystemClassLoader();
                }
                catch (SecurityException securityException) {
                    // empty catch block
                }
                return cl;
            }
        });
    }

    static ClassLoader getParentClassLoader(final ClassLoader cl) {
        return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                ClassLoader parent = null;
                try {
                    parent = cl.getParent();
                }
                catch (SecurityException securityException) {
                    // empty catch block
                }
                return parent == cl ? null : parent;
            }
        });
    }

    static String getSystemProperty(final String propName) {
        return (String)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return System.getProperty(propName);
            }
        });
    }

    static FileInputStream getFileInputStream(final File file) throws FileNotFoundException {
        try {
            return (FileInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() throws FileNotFoundException {
                    return new FileInputStream(file);
                }
            });
        }
        catch (PrivilegedActionException e) {
            throw (FileNotFoundException)e.getException();
        }
    }

    static InputStream getResourceAsStream(final ClassLoader cl, final String name) {
        return (InputStream)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                InputStream ris = cl == null ? ClassLoader.getSystemResourceAsStream(name) : cl.getResourceAsStream(name);
                return ris;
            }
        });
    }

    static boolean getFileExists(final File f) {
        return (Boolean)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return f.exists() ? Boolean.TRUE : Boolean.FALSE;
            }
        });
    }

    static long getLastModified(final File f) {
        return (Long)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return new Long(f.lastModified());
            }
        });
    }

    private SecuritySupport() {
    }
}

