/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.util;

import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class SAAJUtil {
    public static boolean getSystemBoolean(String arg) {
        try {
            return Boolean.getBoolean(arg);
        }
        catch (AccessControlException ex) {
            return false;
        }
    }

    public static Integer getSystemInteger(String arg) {
        try {
            return Integer.getInteger(arg);
        }
        catch (SecurityException ex) {
            return null;
        }
    }

    public static String getSystemProperty(String arg) {
        try {
            return System.getProperty(arg);
        }
        catch (SecurityException ex) {
            return null;
        }
    }

    public static ClassLoader getSystemClassLoader() {
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

            @Override
            public ClassLoader run() {
                return ClassLoader.getSystemClassLoader();
            }
        });
    }
}

