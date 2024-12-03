/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

public class Utils {
    private static boolean useSystemExit = true;
    private static boolean useDirectClassLoading = false;

    public static Class loadClass(String string) throws ClassNotFoundException {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (!useDirectClassLoading && classLoader != null) {
                return classLoader.loadClass(string);
            }
            return Class.forName(string);
        }
        catch (Exception exception) {
            return Class.forName(string);
        }
    }

    public static Object createInstanceOf(String string) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return Utils.loadClass(string).newInstance();
    }

    public static void error(String string) {
        if (useSystemExit) {
            System.exit(1);
        }
        throw new RuntimeException("ANTLR Panic: " + string);
    }

    public static void error(String string, Throwable throwable) {
        if (useSystemExit) {
            System.exit(1);
        }
        throw new RuntimeException("ANTLR Panic", throwable);
    }

    static {
        if ("true".equalsIgnoreCase(System.getProperty("ANTLR_DO_NOT_EXIT", "false"))) {
            useSystemExit = false;
        }
        if ("true".equalsIgnoreCase(System.getProperty("ANTLR_USE_DIRECT_CLASS_LOADING", "false"))) {
            useDirectClassLoading = true;
        }
    }
}

