/*
 * Decompiled with CFR 0.152.
 */
package org.cyberneko.html;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.cyberneko.html.SecuritySupport12;

class SecuritySupport {
    private static final Object securitySupport;

    SecuritySupport() {
    }

    static SecuritySupport getInstance() {
        return (SecuritySupport)securitySupport;
    }

    ClassLoader getContextClassLoader() {
        return null;
    }

    ClassLoader getSystemClassLoader() {
        return null;
    }

    ClassLoader getParentClassLoader(ClassLoader cl) {
        return null;
    }

    String getSystemProperty(String propName) {
        return System.getProperty(propName);
    }

    FileInputStream getFileInputStream(File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }

    InputStream getResourceAsStream(ClassLoader cl, String name) {
        InputStream ris = cl == null ? ClassLoader.getSystemResourceAsStream(name) : cl.getResourceAsStream(name);
        return ris;
    }

    boolean getFileExists(File f) {
        return f.exists();
    }

    long getLastModified(File f) {
        return f.lastModified();
    }

    static {
        SecuritySupport ss = null;
        try {
            Class<?> c = Class.forName("java.security.AccessController");
            ss = new SecuritySupport12();
        }
        catch (Exception exception) {
        }
        finally {
            if (ss == null) {
                ss = new SecuritySupport();
            }
            securitySupport = ss;
        }
    }
}

