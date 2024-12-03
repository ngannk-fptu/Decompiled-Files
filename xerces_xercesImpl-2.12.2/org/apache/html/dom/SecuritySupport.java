/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

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
                ClassLoader classLoader = null;
                try {
                    classLoader = Thread.currentThread().getContextClassLoader();
                }
                catch (SecurityException securityException) {
                    // empty catch block
                }
                return classLoader;
            }
        });
    }

    static ClassLoader getSystemClassLoader() {
        return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                ClassLoader classLoader = null;
                try {
                    classLoader = ClassLoader.getSystemClassLoader();
                }
                catch (SecurityException securityException) {
                    // empty catch block
                }
                return classLoader;
            }
        });
    }

    static ClassLoader getParentClassLoader(final ClassLoader classLoader) {
        return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                ClassLoader classLoader2 = null;
                try {
                    classLoader2 = classLoader.getParent();
                }
                catch (SecurityException securityException) {
                    // empty catch block
                }
                return classLoader2 == classLoader ? null : classLoader2;
            }
        });
    }

    static String getSystemProperty(final String string) {
        return (String)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return System.getProperty(string);
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
        catch (PrivilegedActionException privilegedActionException) {
            throw (FileNotFoundException)privilegedActionException.getException();
        }
    }

    static InputStream getResourceAsStream(final ClassLoader classLoader, final String string) {
        return (InputStream)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                InputStream inputStream = classLoader == null ? ClassLoader.getSystemResourceAsStream(string) : classLoader.getResourceAsStream(string);
                return inputStream;
            }
        });
    }

    static boolean getFileExists(final File file) {
        return (Boolean)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return file.exists() ? Boolean.TRUE : Boolean.FALSE;
            }
        });
    }

    static long getLastModified(final File file) {
        return (Long)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return new Long(file.lastModified());
            }
        });
    }

    private SecuritySupport() {
    }
}

