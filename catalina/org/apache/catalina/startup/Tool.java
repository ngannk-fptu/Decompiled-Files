/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.startup;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.apache.catalina.startup.Bootstrap;
import org.apache.catalina.startup.ClassLoaderFactory;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public final class Tool {
    private static final Log log = LogFactory.getLog(Tool.class);
    private static boolean ant = false;
    private static final String catalinaHome = System.getProperty("catalina.home");
    private static boolean common = false;
    private static boolean server = false;
    private static boolean shared = false;

    public static void main(String[] args) {
        if (catalinaHome == null) {
            log.error((Object)"Must set 'catalina.home' system property");
            System.exit(1);
        }
        int index = 0;
        while (true) {
            if (index == args.length) {
                Tool.usage();
                System.exit(1);
            }
            if ("-ant".equals(args[index])) {
                ant = true;
            } else if ("-common".equals(args[index])) {
                common = true;
            } else if ("-server".equals(args[index])) {
                server = true;
            } else {
                if (!"-shared".equals(args[index])) break;
                shared = true;
            }
            ++index;
        }
        if (index > args.length) {
            Tool.usage();
            System.exit(1);
        }
        if (ant) {
            System.setProperty("ant.home", catalinaHome);
        }
        ClassLoader classLoader = null;
        try {
            ArrayList<File> packed = new ArrayList<File>();
            ArrayList<File> unpacked = new ArrayList<File>();
            unpacked.add(new File(catalinaHome, "classes"));
            packed.add(new File(catalinaHome, "lib"));
            if (common) {
                unpacked.add(new File(catalinaHome, "common" + File.separator + "classes"));
                packed.add(new File(catalinaHome, "common" + File.separator + "lib"));
            }
            if (server) {
                unpacked.add(new File(catalinaHome, "server" + File.separator + "classes"));
                packed.add(new File(catalinaHome, "server" + File.separator + "lib"));
            }
            if (shared) {
                unpacked.add(new File(catalinaHome, "shared" + File.separator + "classes"));
                packed.add(new File(catalinaHome, "shared" + File.separator + "lib"));
            }
            classLoader = ClassLoaderFactory.createClassLoader(unpacked.toArray(new File[0]), packed.toArray(new File[0]), null);
        }
        catch (Throwable t) {
            Bootstrap.handleThrowable(t);
            log.error((Object)"Class loader creation threw exception", t);
            System.exit(1);
        }
        Thread.currentThread().setContextClassLoader(classLoader);
        Class<?> clazz = null;
        String className = args[index++];
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Loading application class " + className));
            }
            clazz = classLoader.loadClass(className);
        }
        catch (Throwable t) {
            Bootstrap.handleThrowable(t);
            log.error((Object)("Exception creating instance of " + className), t);
            System.exit(1);
        }
        Method method = null;
        String[] params = new String[args.length - index];
        System.arraycopy(args, index, params, 0, params.length);
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)"Identifying main() method");
            }
            String methodName = "main";
            Class[] paramTypes = new Class[]{params.getClass()};
            method = clazz.getMethod(methodName, paramTypes);
        }
        catch (Throwable t) {
            Bootstrap.handleThrowable(t);
            log.error((Object)"Exception locating main() method", t);
            System.exit(1);
        }
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)"Calling main() method");
            }
            Object[] paramValues = new Object[]{params};
            method.invoke(null, paramValues);
        }
        catch (Throwable t) {
            t = Bootstrap.unwrapInvocationTargetException(t);
            Bootstrap.handleThrowable(t);
            log.error((Object)"Exception calling main() method", t);
            System.exit(1);
        }
    }

    private static void usage() {
        log.info((Object)"Usage:  java org.apache.catalina.startup.Tool [<options>] <class> [<arguments>]");
    }
}

