/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang;

import java.io.File;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class SystemUtils {
    private static final int JAVA_VERSION_TRIM_SIZE = 3;
    private static final String OS_NAME_WINDOWS_PREFIX = "Windows";
    private static final String USER_HOME_KEY = "user.home";
    private static final String USER_DIR_KEY = "user.dir";
    private static final String JAVA_IO_TMPDIR_KEY = "java.io.tmpdir";
    private static final String JAVA_HOME_KEY = "java.home";
    public static final String AWT_TOOLKIT = SystemUtils.getSystemProperty("awt.toolkit");
    public static final String FILE_ENCODING = SystemUtils.getSystemProperty("file.encoding");
    public static final String FILE_SEPARATOR = SystemUtils.getSystemProperty("file.separator");
    public static final String JAVA_AWT_FONTS = SystemUtils.getSystemProperty("java.awt.fonts");
    public static final String JAVA_AWT_GRAPHICSENV = SystemUtils.getSystemProperty("java.awt.graphicsenv");
    public static final String JAVA_AWT_HEADLESS = SystemUtils.getSystemProperty("java.awt.headless");
    public static final String JAVA_AWT_PRINTERJOB = SystemUtils.getSystemProperty("java.awt.printerjob");
    public static final String JAVA_CLASS_PATH = SystemUtils.getSystemProperty("java.class.path");
    public static final String JAVA_CLASS_VERSION = SystemUtils.getSystemProperty("java.class.version");
    public static final String JAVA_COMPILER = SystemUtils.getSystemProperty("java.compiler");
    public static final String JAVA_ENDORSED_DIRS = SystemUtils.getSystemProperty("java.endorsed.dirs");
    public static final String JAVA_EXT_DIRS = SystemUtils.getSystemProperty("java.ext.dirs");
    public static final String JAVA_HOME = SystemUtils.getSystemProperty("java.home");
    public static final String JAVA_IO_TMPDIR = SystemUtils.getSystemProperty("java.io.tmpdir");
    public static final String JAVA_LIBRARY_PATH = SystemUtils.getSystemProperty("java.library.path");
    public static final String JAVA_RUNTIME_NAME = SystemUtils.getSystemProperty("java.runtime.name");
    public static final String JAVA_RUNTIME_VERSION = SystemUtils.getSystemProperty("java.runtime.version");
    public static final String JAVA_SPECIFICATION_NAME = SystemUtils.getSystemProperty("java.specification.name");
    public static final String JAVA_SPECIFICATION_VENDOR = SystemUtils.getSystemProperty("java.specification.vendor");
    public static final String JAVA_SPECIFICATION_VERSION = SystemUtils.getSystemProperty("java.specification.version");
    public static final String JAVA_UTIL_PREFS_PREFERENCES_FACTORY = SystemUtils.getSystemProperty("java.util.prefs.PreferencesFactory");
    public static final String JAVA_VENDOR = SystemUtils.getSystemProperty("java.vendor");
    public static final String JAVA_VENDOR_URL = SystemUtils.getSystemProperty("java.vendor.url");
    public static final String JAVA_VERSION = SystemUtils.getSystemProperty("java.version");
    public static final String JAVA_VM_INFO = SystemUtils.getSystemProperty("java.vm.info");
    public static final String JAVA_VM_NAME = SystemUtils.getSystemProperty("java.vm.name");
    public static final String JAVA_VM_SPECIFICATION_NAME = SystemUtils.getSystemProperty("java.vm.specification.name");
    public static final String JAVA_VM_SPECIFICATION_VENDOR = SystemUtils.getSystemProperty("java.vm.specification.vendor");
    public static final String JAVA_VM_SPECIFICATION_VERSION = SystemUtils.getSystemProperty("java.vm.specification.version");
    public static final String JAVA_VM_VENDOR = SystemUtils.getSystemProperty("java.vm.vendor");
    public static final String JAVA_VM_VERSION = SystemUtils.getSystemProperty("java.vm.version");
    public static final String LINE_SEPARATOR = SystemUtils.getSystemProperty("line.separator");
    public static final String OS_ARCH = SystemUtils.getSystemProperty("os.arch");
    public static final String OS_NAME = SystemUtils.getSystemProperty("os.name");
    public static final String OS_VERSION = SystemUtils.getSystemProperty("os.version");
    public static final String PATH_SEPARATOR = SystemUtils.getSystemProperty("path.separator");
    public static final String USER_COUNTRY = SystemUtils.getSystemProperty("user.country") == null ? SystemUtils.getSystemProperty("user.region") : SystemUtils.getSystemProperty("user.country");
    public static final String USER_DIR = SystemUtils.getSystemProperty("user.dir");
    public static final String USER_HOME = SystemUtils.getSystemProperty("user.home");
    public static final String USER_LANGUAGE = SystemUtils.getSystemProperty("user.language");
    public static final String USER_NAME = SystemUtils.getSystemProperty("user.name");
    public static final String USER_TIMEZONE = SystemUtils.getSystemProperty("user.timezone");
    public static final String JAVA_VERSION_TRIMMED = SystemUtils.getJavaVersionTrimmed();
    public static final float JAVA_VERSION_FLOAT = SystemUtils.getJavaVersionAsFloat();
    public static final int JAVA_VERSION_INT = SystemUtils.getJavaVersionAsInt();
    public static final boolean IS_JAVA_1_1 = SystemUtils.getJavaVersionMatches("1.1");
    public static final boolean IS_JAVA_1_2 = SystemUtils.getJavaVersionMatches("1.2");
    public static final boolean IS_JAVA_1_3 = SystemUtils.getJavaVersionMatches("1.3");
    public static final boolean IS_JAVA_1_4 = SystemUtils.getJavaVersionMatches("1.4");
    public static final boolean IS_JAVA_1_5 = SystemUtils.getJavaVersionMatches("1.5");
    public static final boolean IS_JAVA_1_6 = SystemUtils.getJavaVersionMatches("1.6");
    public static final boolean IS_JAVA_1_7 = SystemUtils.getJavaVersionMatches("1.7");
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
    public static final boolean IS_OS_WINDOWS_2000 = SystemUtils.getOSMatches("Windows", "5.0");
    public static final boolean IS_OS_WINDOWS_95 = SystemUtils.getOSMatches("Windows 9", "4.0");
    public static final boolean IS_OS_WINDOWS_98 = SystemUtils.getOSMatches("Windows 9", "4.1");
    public static final boolean IS_OS_WINDOWS_ME = SystemUtils.getOSMatches("Windows", "4.9");
    public static final boolean IS_OS_WINDOWS_NT = SystemUtils.getOSMatchesName("Windows NT");
    public static final boolean IS_OS_WINDOWS_XP = SystemUtils.getOSMatches("Windows", "5.1");
    public static final boolean IS_OS_WINDOWS_VISTA = SystemUtils.getOSMatches("Windows", "6.0");
    public static final boolean IS_OS_WINDOWS_7 = SystemUtils.getOSMatches("Windows", "6.1");

    public static File getJavaHome() {
        return new File(System.getProperty(JAVA_HOME_KEY));
    }

    public static File getJavaIoTmpDir() {
        return new File(System.getProperty(JAVA_IO_TMPDIR_KEY));
    }

    public static float getJavaVersion() {
        return JAVA_VERSION_FLOAT;
    }

    private static float getJavaVersionAsFloat() {
        return SystemUtils.toVersionFloat(SystemUtils.toJavaVersionIntArray(JAVA_VERSION, 3));
    }

    private static int getJavaVersionAsInt() {
        return SystemUtils.toVersionInt(SystemUtils.toJavaVersionIntArray(JAVA_VERSION, 3));
    }

    private static boolean getJavaVersionMatches(String versionPrefix) {
        return SystemUtils.isJavaVersionMatch(JAVA_VERSION_TRIMMED, versionPrefix);
    }

    private static String getJavaVersionTrimmed() {
        if (JAVA_VERSION != null) {
            for (int i = 0; i < JAVA_VERSION.length(); ++i) {
                char ch = JAVA_VERSION.charAt(i);
                if (ch < '0' || ch > '9') continue;
                return JAVA_VERSION.substring(i);
            }
        }
        return null;
    }

    private static boolean getOSMatches(String osNamePrefix, String osVersionPrefix) {
        return SystemUtils.isOSMatch(OS_NAME, OS_VERSION, osNamePrefix, osVersionPrefix);
    }

    private static boolean getOSMatchesName(String osNamePrefix) {
        return SystemUtils.isOSNameMatch(OS_NAME, osNamePrefix);
    }

    private static String getSystemProperty(String property) {
        try {
            return System.getProperty(property);
        }
        catch (SecurityException ex) {
            System.err.println("Caught a SecurityException reading the system property '" + property + "'; the SystemUtils property value will default to null.");
            return null;
        }
    }

    public static File getUserDir() {
        return new File(System.getProperty(USER_DIR_KEY));
    }

    public static File getUserHome() {
        return new File(System.getProperty(USER_HOME_KEY));
    }

    public static boolean isJavaAwtHeadless() {
        return JAVA_AWT_HEADLESS != null ? JAVA_AWT_HEADLESS.equals(Boolean.TRUE.toString()) : false;
    }

    public static boolean isJavaVersionAtLeast(float requiredVersion) {
        return JAVA_VERSION_FLOAT >= requiredVersion;
    }

    public static boolean isJavaVersionAtLeast(int requiredVersion) {
        return JAVA_VERSION_INT >= requiredVersion;
    }

    static boolean isJavaVersionMatch(String version, String versionPrefix) {
        if (version == null) {
            return false;
        }
        return version.startsWith(versionPrefix);
    }

    static boolean isOSMatch(String osName, String osVersion, String osNamePrefix, String osVersionPrefix) {
        if (osName == null || osVersion == null) {
            return false;
        }
        return osName.startsWith(osNamePrefix) && osVersion.startsWith(osVersionPrefix);
    }

    static boolean isOSNameMatch(String osName, String osNamePrefix) {
        if (osName == null) {
            return false;
        }
        return osName.startsWith(osNamePrefix);
    }

    static float toJavaVersionFloat(String version) {
        return SystemUtils.toVersionFloat(SystemUtils.toJavaVersionIntArray(version, 3));
    }

    static int toJavaVersionInt(String version) {
        return SystemUtils.toVersionInt(SystemUtils.toJavaVersionIntArray(version, 3));
    }

    static int[] toJavaVersionIntArray(String version) {
        return SystemUtils.toJavaVersionIntArray(version, Integer.MAX_VALUE);
    }

    private static int[] toJavaVersionIntArray(String version, int limit) {
        if (version == null) {
            return ArrayUtils.EMPTY_INT_ARRAY;
        }
        String[] strings = StringUtils.split(version, "._- ");
        int[] ints = new int[Math.min(limit, strings.length)];
        int j = 0;
        for (int i = 0; i < strings.length && j < limit; ++i) {
            String s = strings[i];
            if (s.length() <= 0) continue;
            try {
                ints[j] = Integer.parseInt(s);
                ++j;
                continue;
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        if (ints.length > j) {
            int[] newInts = new int[j];
            System.arraycopy(ints, 0, newInts, 0, j);
            ints = newInts;
        }
        return ints;
    }

    private static float toVersionFloat(int[] javaVersions) {
        if (javaVersions == null || javaVersions.length == 0) {
            return 0.0f;
        }
        if (javaVersions.length == 1) {
            return javaVersions[0];
        }
        StringBuffer builder = new StringBuffer();
        builder.append(javaVersions[0]);
        builder.append('.');
        for (int i = 1; i < javaVersions.length; ++i) {
            builder.append(javaVersions[i]);
        }
        try {
            return Float.parseFloat(builder.toString());
        }
        catch (Exception ex) {
            return 0.0f;
        }
    }

    private static int toVersionInt(int[] javaVersions) {
        if (javaVersions == null) {
            return 0;
        }
        int intVersion = 0;
        int len = javaVersions.length;
        if (len >= 1) {
            intVersion = javaVersions[0] * 100;
        }
        if (len >= 2) {
            intVersion += javaVersions[1] * 10;
        }
        if (len >= 3) {
            intVersion += javaVersions[2];
        }
        return intVersion;
    }
}

