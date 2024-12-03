/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.util;

import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.ReflectionUtil;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class EnvUtil {
    private EnvUtil() {
    }

    public static String logbackVersion() {
        String moduleVersion = EnvUtil.logbackVersionByModule();
        if (moduleVersion != null) {
            return moduleVersion;
        }
        Package pkg = EnvUtil.class.getPackage();
        if (pkg == null) {
            return null;
        }
        return pkg.getImplementationVersion();
    }

    private static String logbackVersionByModule() {
        if (!EnvUtil.isJDK9OrHigher()) {
            return null;
        }
        try {
            Object moduleObject = ReflectionUtil.invokeMethodOnObject(EnvUtil.class, "getModule");
            if (moduleObject == null) {
                return null;
            }
            Object moduleDescriptorObject = ReflectionUtil.invokeMethodOnObject(moduleObject, "getDescriptor");
            if (moduleDescriptorObject == null) {
                return null;
            }
            Object optionalStringObject = ReflectionUtil.invokeMethodOnObject(moduleDescriptorObject, "rawVersion");
            if (optionalStringObject == null) {
                return null;
            }
            Optional opt = (Optional)optionalStringObject;
            return opt.orElse(null);
        }
        catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            return null;
        }
    }

    public static int getJDKVersion(String javaVersionStr) {
        int version = 0;
        for (char ch : javaVersionStr.toCharArray()) {
            if (Character.isDigit(ch)) {
                version = version * 10 + (ch - 48);
                continue;
            }
            if (version != 1) break;
            version = 0;
        }
        return version;
    }

    private static boolean isJDK_N_OrHigher(int n) {
        String javaVersionStr = System.getProperty("java.version", "");
        if (javaVersionStr.isEmpty()) {
            return false;
        }
        int version = EnvUtil.getJDKVersion(javaVersionStr);
        return version > 0 && n <= version;
    }

    public static boolean isJDK5() {
        return EnvUtil.isJDK_N_OrHigher(5);
    }

    public static boolean isJDK6OrHigher() {
        return EnvUtil.isJDK_N_OrHigher(6);
    }

    public static boolean isJDK7OrHigher() {
        return EnvUtil.isJDK_N_OrHigher(7);
    }

    public static boolean isJDK9OrHigher() {
        return EnvUtil.isJDK_N_OrHigher(9);
    }

    public static boolean isJDK16OrHigher() {
        return EnvUtil.isJDK_N_OrHigher(16);
    }

    public static boolean isJDK18OrHigher() {
        return EnvUtil.isJDK_N_OrHigher(18);
    }

    public static boolean isJDK21OrHigher() {
        return EnvUtil.isJDK_N_OrHigher(21);
    }

    public static boolean isJaninoAvailable() {
        ClassLoader classLoader = EnvUtil.class.getClassLoader();
        try {
            Class<?> bindingClass = classLoader.loadClass("org.codehaus.janino.ScriptEvaluator");
            return bindingClass != null;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isWindows() {
        String os = System.getProperty("os.name");
        return os.startsWith("Windows");
    }

    public static boolean isClassAvailable(Class callerClass, String className) {
        ClassLoader classLoader = Loader.getClassLoaderOfClass(callerClass);
        try {
            Class<?> bindingClass = classLoader.loadClass(className);
            return bindingClass != null;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }
}

