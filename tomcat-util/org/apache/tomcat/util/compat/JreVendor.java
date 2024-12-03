/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.compat;

import java.util.Locale;

public class JreVendor {
    public static final boolean IS_ORACLE_JVM;
    public static final boolean IS_IBM_JVM;

    static {
        String vendor = System.getProperty("java.vendor", "");
        if ((vendor = vendor.toLowerCase(Locale.ENGLISH)).startsWith("oracle") || vendor.startsWith("sun")) {
            IS_ORACLE_JVM = true;
            IS_IBM_JVM = false;
        } else if (vendor.contains("ibm")) {
            IS_ORACLE_JVM = false;
            IS_IBM_JVM = true;
        } else {
            IS_ORACLE_JVM = false;
            IS_IBM_JVM = false;
        }
    }
}

