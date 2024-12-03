/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.xpath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;

final class SecuritySupport {
    private SecuritySupport() {
    }

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

    static InputStream getURLInputStream(final URL uRL) throws IOException {
        try {
            return (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() throws IOException {
                    return uRL.openStream();
                }
            });
        }
        catch (PrivilegedActionException privilegedActionException) {
            throw (IOException)privilegedActionException.getException();
        }
    }

    static URL getResourceAsURL(final ClassLoader classLoader, final String string) {
        return (URL)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                URL uRL = classLoader == null ? ClassLoader.getSystemResource(string) : classLoader.getResource(string);
                return uRL;
            }
        });
    }

    static Enumeration getResources(final ClassLoader classLoader, final String string) throws IOException {
        try {
            return (Enumeration)AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() throws IOException {
                    Enumeration<URL> enumeration = classLoader == null ? ClassLoader.getSystemResources(string) : classLoader.getResources(string);
                    return enumeration;
                }
            });
        }
        catch (PrivilegedActionException privilegedActionException) {
            throw (IOException)privilegedActionException.getException();
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

    static boolean doesFileExist(final File file) {
        return (Boolean)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return file.exists() ? Boolean.TRUE : Boolean.FALSE;
            }
        });
    }
}

