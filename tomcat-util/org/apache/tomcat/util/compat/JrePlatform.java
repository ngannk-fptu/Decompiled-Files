/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.compat;

import java.security.AccessController;
import java.util.Locale;

public class JrePlatform {
    private static final String OS_NAME_PROPERTY = "os.name";
    public static final boolean IS_MAC_OS;
    public static final boolean IS_WINDOWS;

    static {
        String osName = System.getSecurityManager() == null ? System.getProperty(OS_NAME_PROPERTY) : AccessController.doPrivileged(() -> System.getProperty(OS_NAME_PROPERTY));
        IS_MAC_OS = osName.toLowerCase(Locale.ENGLISH).startsWith("mac os x");
        IS_WINDOWS = osName.startsWith("Windows");
    }
}

