/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.LucenePackage;
import java.lang.reflect.Field;

public final class Constants {
    public static final String JVM_VENDOR;
    public static final String JVM_VERSION;
    public static final String JVM_NAME;
    public static final String JAVA_VERSION;
    @Deprecated
    public static final boolean JAVA_1_1;
    @Deprecated
    public static final boolean JAVA_1_2;
    @Deprecated
    public static final boolean JAVA_1_3;
    public static final String OS_NAME;
    public static final boolean LINUX;
    public static final boolean WINDOWS;
    public static final boolean SUN_OS;
    public static final boolean MAC_OS_X;
    public static final String OS_ARCH;
    public static final String OS_VERSION;
    public static final String JAVA_VENDOR;
    public static final boolean JRE_IS_MINIMUM_JAVA6;
    public static final boolean JRE_IS_MINIMUM_JAVA7;
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
        JAVA_1_1 = JAVA_VERSION.startsWith("1.1.");
        JAVA_1_2 = JAVA_VERSION.startsWith("1.2.");
        JAVA_1_3 = JAVA_VERSION.startsWith("1.3.");
        OS_NAME = System.getProperty("os.name");
        LINUX = OS_NAME.startsWith("Linux");
        WINDOWS = OS_NAME.startsWith("Windows");
        SUN_OS = OS_NAME.startsWith("SunOS");
        MAC_OS_X = OS_NAME.startsWith("Mac OS X");
        OS_ARCH = System.getProperty("os.arch");
        OS_VERSION = System.getProperty("os.version");
        JAVA_VENDOR = System.getProperty("java.vendor");
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
        boolean v6 = true;
        try {
            String.class.getMethod("isEmpty", new Class[0]);
        }
        catch (NoSuchMethodException nsme) {
            v6 = false;
        }
        JRE_IS_MINIMUM_JAVA6 = v6;
        boolean v7 = true;
        try {
            Throwable.class.getMethod("getSuppressed", new Class[0]);
        }
        catch (NoSuchMethodException nsme) {
            v7 = false;
        }
        JRE_IS_MINIMUM_JAVA7 = v7;
        LUCENE_MAIN_VERSION = Constants.ident("3.6.2");
        Package pkg = LucenePackage.get();
        String string = v = pkg == null ? null : pkg.getImplementationVersion();
        if (v == null) {
            v = LUCENE_MAIN_VERSION + "-SNAPSHOT";
        } else if (!v.startsWith(LUCENE_MAIN_VERSION)) {
            v = LUCENE_MAIN_VERSION + "-SNAPSHOT " + v;
        }
        LUCENE_VERSION = Constants.ident(v);
    }
}

