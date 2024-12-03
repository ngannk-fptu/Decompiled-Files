/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang3;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;

public final class SystemProperties {
    private static final Supplier<String> NULL_SUPPLIER = () -> null;
    public static final String AWT_TOOLKIT = "awt.toolkit";
    public static final String FILE_ENCODING = "file.encoding";
    public static final String FILE_SEPARATOR = "file.separator";
    public static final String JAVA_AWT_FONTS = "java.awt.fonts";
    public static final String JAVA_AWT_GRAPHICSENV = "java.awt.graphicsenv";
    public static final String JAVA_AWT_HEADLESS = "java.awt.headless";
    public static final String JAVA_AWT_PRINTERJOB = "java.awt.printerjob";
    public static final String JAVA_CLASS_PATH = "java.class.path";
    public static final String JAVA_CLASS_VERSION = "java.class.version";
    public static final String JAVA_COMPILER = "java.compiler";
    public static final String JAVA_ENDORSED_DIRS = "java.endorsed.dirs";
    public static final String JAVA_EXT_DIRS = "java.ext.dirs";
    public static final String JAVA_HOME = "java.home";
    public static final String JAVA_IO_TMPDIR = "java.io.tmpdir";
    public static final String JAVA_LIBRARY_PATH = "java.library.path";
    public static final String JAVA_LOCALE_PROVIDERS = "java.locale.providers";
    public static final String JAVA_RUNTIME_NAME = "java.runtime.name";
    public static final String JAVA_RUNTIME_VERSION = "java.runtime.version";
    public static final String JAVA_SPECIFICATION_NAME = "java.specification.name";
    public static final String JAVA_SPECIFICATION_VENDOR = "java.specification.vendor";
    public static final String JAVA_SPECIFICATION_VERSION = "java.specification.version";
    public static final String JAVA_UTIL_PREFS_PREFERENCES_FACTORY = "java.util.prefs.PreferencesFactory";
    public static final String JAVA_VENDOR = "java.vendor";
    public static final String JAVA_VENDOR_URL = "java.vendor.url";
    public static final String JAVA_VERSION = "java.version";
    public static final String JAVA_VM_INFO = "java.vm.info";
    public static final String JAVA_VM_NAME = "java.vm.name";
    public static final String JAVA_VM_SPECIFICATION_NAME = "java.vm.specification.name";
    public static final String JAVA_VM_SPECIFICATION_VENDOR = "java.vm.specification.vendor";
    public static final String JAVA_VM_SPECIFICATION_VERSION = "java.vm.specification.version";
    public static final String JAVA_VM_VENDOR = "java.vm.vendor";
    public static final String JAVA_VM_VERSION = "java.vm.version";
    public static final String LINE_SEPARATOR = "line.separator";
    public static final String OS_ARCH = "os.arch";
    public static final String OS_NAME = "os.name";
    public static final String OS_VERSION = "os.version";
    public static final String PATH_SEPARATOR = "path.separator";
    public static final String USER_COUNTRY = "user.country";
    public static final String USER_DIR = "user.dir";
    public static final String USER_HOME = "user.home";
    public static final String USER_LANGUAGE = "user.language";
    public static final String USER_NAME = "user.name";
    public static final String USER_REGION = "user.region";
    public static final String USER_TIMEZONE = "user.timezone";

    public static String getAwtToolkit() {
        return SystemProperties.getProperty(AWT_TOOLKIT);
    }

    public static boolean getBoolean(String key, BooleanSupplier defaultIfAbsent) {
        String str = SystemProperties.getProperty(key);
        return str == null ? defaultIfAbsent != null && defaultIfAbsent.getAsBoolean() : Boolean.parseBoolean(str);
    }

    public static String getFileEncoding() {
        return SystemProperties.getProperty(FILE_ENCODING);
    }

    public static String getFileSeparator() {
        return SystemProperties.getProperty(FILE_SEPARATOR);
    }

    public static int getInt(String key, IntSupplier defaultIfAbsent) {
        String str = SystemProperties.getProperty(key);
        return str == null ? (defaultIfAbsent != null ? defaultIfAbsent.getAsInt() : 0) : Integer.parseInt(str);
    }

    public static String getJavaAwtFonts() {
        return SystemProperties.getProperty(JAVA_AWT_FONTS);
    }

    public static String getJavaAwtGraphicsenv() {
        return SystemProperties.getProperty(JAVA_AWT_GRAPHICSENV);
    }

    public static String getJavaAwtHeadless() {
        return SystemProperties.getProperty(JAVA_AWT_HEADLESS);
    }

    public static String getJavaAwtPrinterjob() {
        return SystemProperties.getProperty(JAVA_AWT_PRINTERJOB);
    }

    public static String getJavaClassPath() {
        return SystemProperties.getProperty(JAVA_CLASS_PATH);
    }

    public static String getJavaClassVersion() {
        return SystemProperties.getProperty(JAVA_CLASS_VERSION);
    }

    public static String getJavaCompiler() {
        return SystemProperties.getProperty(JAVA_COMPILER);
    }

    public static String getJavaEndorsedDirs() {
        return SystemProperties.getProperty(JAVA_ENDORSED_DIRS);
    }

    public static String getJavaExtDirs() {
        return SystemProperties.getProperty(JAVA_EXT_DIRS);
    }

    public static String getJavaHome() {
        return SystemProperties.getProperty(JAVA_HOME);
    }

    public static String getJavaIoTmpdir() {
        return SystemProperties.getProperty(JAVA_IO_TMPDIR);
    }

    public static String getJavaLibraryPath() {
        return SystemProperties.getProperty(JAVA_LIBRARY_PATH);
    }

    public static String getJavaLocaleProviders() {
        return SystemProperties.getProperty(JAVA_LOCALE_PROVIDERS);
    }

    public static String getJavaRuntimeName() {
        return SystemProperties.getProperty(JAVA_RUNTIME_NAME);
    }

    public static String getJavaRuntimeVersion() {
        return SystemProperties.getProperty(JAVA_RUNTIME_VERSION);
    }

    public static String getJavaSpecificationName() {
        return SystemProperties.getProperty(JAVA_SPECIFICATION_NAME);
    }

    public static String getJavaSpecificationVendor() {
        return SystemProperties.getProperty(JAVA_SPECIFICATION_VENDOR);
    }

    public static String getJavaSpecificationVersion() {
        return SystemProperties.getProperty(JAVA_SPECIFICATION_VERSION);
    }

    public static String getJavaUtilPrefsPreferencesFactory() {
        return SystemProperties.getProperty(JAVA_UTIL_PREFS_PREFERENCES_FACTORY);
    }

    public static String getJavaVendor() {
        return SystemProperties.getProperty(JAVA_VENDOR);
    }

    public static String getJavaVendorUrl() {
        return SystemProperties.getProperty(JAVA_VENDOR_URL);
    }

    public static String getJavaVersion() {
        return SystemProperties.getProperty(JAVA_VERSION);
    }

    public static String getJavaVmInfo() {
        return SystemProperties.getProperty(JAVA_VM_INFO);
    }

    public static String getJavaVmName() {
        return SystemProperties.getProperty(JAVA_VM_NAME);
    }

    public static String getJavaVmSpecificationName() {
        return SystemProperties.getProperty(JAVA_VM_SPECIFICATION_NAME);
    }

    public static String getJavaVmSpecificationVendor() {
        return SystemProperties.getProperty(JAVA_VM_SPECIFICATION_VENDOR);
    }

    public static String getJavaVmSpecificationVersion() {
        return SystemProperties.getProperty(JAVA_VM_SPECIFICATION_VERSION);
    }

    public static String getJavaVmVendor() {
        return SystemProperties.getProperty(JAVA_VM_VENDOR);
    }

    public static String getJavaVmVersion() {
        return SystemProperties.getProperty(JAVA_VM_VERSION);
    }

    public static String getLineSeparator() {
        return SystemProperties.getProperty(LINE_SEPARATOR);
    }

    public static long getLong(String key, LongSupplier defaultIfAbsent) {
        String str = SystemProperties.getProperty(key);
        return str == null ? (defaultIfAbsent != null ? defaultIfAbsent.getAsLong() : 0L) : Long.parseLong(str);
    }

    public static String getOsArch() {
        return SystemProperties.getProperty(OS_ARCH);
    }

    public static String getOsName() {
        return SystemProperties.getProperty(OS_NAME);
    }

    public static String getOsVersion() {
        return SystemProperties.getProperty(OS_VERSION);
    }

    public static String getPathSeparator() {
        return SystemProperties.getProperty(PATH_SEPARATOR);
    }

    public static String getProperty(String property) {
        return SystemProperties.getProperty(property, NULL_SUPPLIER);
    }

    static String getProperty(String property, Supplier<String> defaultValue) {
        try {
            if (StringUtils.isEmpty(property)) {
                return defaultValue.get();
            }
            String value = System.getProperty(property);
            return StringUtils.getIfEmpty(value, defaultValue);
        }
        catch (SecurityException ignore) {
            return defaultValue.get();
        }
    }

    public static String getUserCountry() {
        return SystemProperties.getProperty(USER_COUNTRY);
    }

    public static String getUserDir() {
        return SystemProperties.getProperty(USER_DIR);
    }

    public static String getUserHome() {
        return SystemProperties.getProperty(USER_HOME);
    }

    public static String getUserLanguage() {
        return SystemProperties.getProperty(USER_LANGUAGE);
    }

    public static String getUserName() {
        return SystemProperties.getProperty(USER_NAME);
    }

    public static String getUserTimezone() {
        return SystemProperties.getProperty(USER_TIMEZONE);
    }
}

