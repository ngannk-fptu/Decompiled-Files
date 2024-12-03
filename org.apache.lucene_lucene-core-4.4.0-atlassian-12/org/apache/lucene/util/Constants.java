/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.lang.reflect.Field;
import java.util.Collections;
import org.apache.lucene.LucenePackage;

public final class Constants {
    public static final String JVM_VENDOR;
    public static final String JVM_VERSION;
    public static final String JVM_NAME;
    public static final String JAVA_VERSION;
    public static final String OS_NAME;
    public static final boolean LINUX;
    public static final boolean WINDOWS;
    public static final boolean SUN_OS;
    public static final boolean MAC_OS_X;
    public static final boolean FREE_BSD;
    public static final String OS_ARCH;
    public static final String OS_VERSION;
    public static final String JAVA_VENDOR;
    @Deprecated
    public static final boolean JRE_IS_MINIMUM_JAVA6;
    public static final boolean JRE_IS_MINIMUM_JAVA7;
    public static final boolean JRE_IS_MINIMUM_JAVA8;
    public static final boolean JRE_IS_64BIT;
    public static final String LUCENE_MAIN_VERSION;
    public static final String LUCENE_VERSION;

    private Constants() {
    }

    private static String ident(String s) {
        return s.toString();
    }

    static {
        String v;
        JVM_VENDOR = System.getProperty("java.vm.vendor");
        JVM_VERSION = System.getProperty("java.vm.version");
        JVM_NAME = System.getProperty("java.vm.name");
        JAVA_VERSION = System.getProperty("java.version");
        OS_NAME = System.getProperty("os.name");
        LINUX = OS_NAME.startsWith("Linux");
        WINDOWS = OS_NAME.startsWith("Windows");
        SUN_OS = OS_NAME.startsWith("SunOS");
        MAC_OS_X = OS_NAME.startsWith("Mac OS X");
        FREE_BSD = OS_NAME.startsWith("FreeBSD");
        OS_ARCH = System.getProperty("os.arch");
        OS_VERSION = System.getProperty("os.version");
        JAVA_VENDOR = System.getProperty("java.vendor");
        JRE_IS_MINIMUM_JAVA6 = new Boolean(true);
        boolean is64Bit = false;
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Object unsafe = unsafeField.get(null);
            int addressSize = ((Number)unsafeClass.getMethod("addressSize", new Class[0]).invoke(unsafe, new Object[0])).intValue();
            is64Bit = addressSize >= 8;
        }
        catch (Exception e) {
            String x = System.getProperty("sun.arch.data.model");
            is64Bit = x != null ? x.indexOf("64") != -1 : OS_ARCH != null && OS_ARCH.indexOf("64") != -1;
        }
        JRE_IS_64BIT = is64Bit;
        boolean v7 = true;
        try {
            Throwable.class.getMethod("getSuppressed", new Class[0]);
        }
        catch (NoSuchMethodException nsme) {
            v7 = false;
        }
        JRE_IS_MINIMUM_JAVA7 = v7;
        if (JRE_IS_MINIMUM_JAVA7) {
            boolean v8 = true;
            try {
                Collections.class.getMethod("emptySortedSet", new Class[0]);
            }
            catch (NoSuchMethodException nsme) {
                v8 = false;
            }
            JRE_IS_MINIMUM_JAVA8 = v8;
        } else {
            JRE_IS_MINIMUM_JAVA8 = false;
        }
        LUCENE_MAIN_VERSION = Constants.ident("4.4");
        Package pkg = LucenePackage.get();
        String string = v = pkg == null ? null : pkg.getImplementationVersion();
        if (v == null) {
            String[] parts = LUCENE_MAIN_VERSION.split("\\.");
            if (parts.length == 4) {
                assert (parts[2].equals("0"));
                v = parts[0] + "." + parts[1] + "-SNAPSHOT";
            } else {
                v = LUCENE_MAIN_VERSION + "-SNAPSHOT";
            }
        }
        LUCENE_VERSION = Constants.ident(v);
    }
}

