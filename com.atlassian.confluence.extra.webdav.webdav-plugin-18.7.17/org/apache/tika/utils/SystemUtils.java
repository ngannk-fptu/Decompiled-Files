/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.utils;

public class SystemUtils {
    public static final String OS_NAME = SystemUtils.getSystemProperty("os.name");
    public static final String OS_VERSION = SystemUtils.getSystemProperty("os.version");
    public static final boolean IS_OS_AIX = SystemUtils.getOSMatchesName("AIX");
    public static final boolean IS_OS_HP_UX = SystemUtils.getOSMatchesName("HP-UX");
    public static final boolean IS_OS_IRIX = SystemUtils.getOSMatchesName("Irix");
    public static final boolean IS_OS_LINUX = SystemUtils.getOSMatchesName("Linux") || SystemUtils.getOSMatchesName("LINUX");
    public static final boolean IS_OS_MAC = SystemUtils.getOSMatchesName("Mac");
    public static final boolean IS_OS_MAC_OSX = SystemUtils.getOSMatchesName("Mac OS X");
    public static final boolean IS_OS_OS2 = SystemUtils.getOSMatchesName("OS/2");
    public static final boolean IS_OS_SOLARIS = SystemUtils.getOSMatchesName("Solaris");
    public static final boolean IS_OS_SUN_OS = SystemUtils.getOSMatchesName("SunOS");
    public static final boolean IS_OS_UNIX = IS_OS_AIX || IS_OS_HP_UX || IS_OS_IRIX || IS_OS_LINUX || IS_OS_MAC_OSX || IS_OS_SOLARIS || IS_OS_SUN_OS;
    public static final boolean IS_OS_WINDOWS = SystemUtils.getOSMatchesName("Windows");
    private static final String OS_NAME_WINDOWS_PREFIX = "Windows";

    private static String getSystemProperty(String property) {
        try {
            return System.getProperty(property);
        }
        catch (SecurityException var2) {
            return null;
        }
    }

    private static boolean getOSMatchesName(String osNamePrefix) {
        return SystemUtils.isOSNameMatch(OS_NAME, osNamePrefix);
    }

    static boolean isOSNameMatch(String osName, String osNamePrefix) {
        return osName != null && osName.startsWith(osNamePrefix);
    }
}

