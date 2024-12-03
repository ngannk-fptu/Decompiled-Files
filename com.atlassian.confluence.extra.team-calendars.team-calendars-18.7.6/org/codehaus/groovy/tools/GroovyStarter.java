/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools;

import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.codehaus.groovy.tools.LoaderConfiguration;
import org.codehaus.groovy.tools.RootLoader;

public class GroovyStarter {
    static void printUsage() {
        System.out.println("possible programs are 'groovyc','groovy','console', and 'groovysh'");
        System.exit(1);
    }

    public static void rootLoader(String[] args) {
        String confOverride;
        String conf = System.getProperty("groovy.starter.conf", null);
        final LoaderConfiguration lc = new LoaderConfiguration();
        boolean hadMain = false;
        boolean hadConf = false;
        boolean hadCP = false;
        int argsOffset = 0;
        while (!(args.length - argsOffset <= 0 || hadMain && hadConf && hadCP)) {
            if (args[argsOffset].equals("--classpath")) {
                if (hadCP) break;
                if (args.length == argsOffset + 1) {
                    GroovyStarter.exit("classpath parameter needs argument");
                }
                lc.addClassPath(args[argsOffset + 1]);
                argsOffset += 2;
                hadCP = true;
                continue;
            }
            if (args[argsOffset].equals("--main")) {
                if (hadMain) break;
                if (args.length == argsOffset + 1) {
                    GroovyStarter.exit("main parameter needs argument");
                }
                lc.setMainClass(args[argsOffset + 1]);
                argsOffset += 2;
                hadMain = true;
                continue;
            }
            if (!args[argsOffset].equals("--conf") || hadConf) break;
            if (args.length == argsOffset + 1) {
                GroovyStarter.exit("conf parameter needs argument");
            }
            conf = args[argsOffset + 1];
            argsOffset += 2;
            hadConf = true;
        }
        if ((confOverride = System.getProperty("groovy.starter.conf.override", null)) != null) {
            conf = confOverride;
        }
        if (lc.getMainClass() == null && conf == null) {
            GroovyStarter.exit("no configuration file or main class specified");
        }
        String[] newArgs = new String[args.length - argsOffset];
        for (int i = 0; i < newArgs.length; ++i) {
            newArgs[i] = args[i + argsOffset];
        }
        if (conf != null) {
            try {
                lc.configure(new FileInputStream(conf));
            }
            catch (Exception e) {
                System.err.println("exception while configuring main class loader:");
                GroovyStarter.exit(e);
            }
        }
        ClassLoader loader = AccessController.doPrivileged(new PrivilegedAction<RootLoader>(){

            @Override
            public RootLoader run() {
                return new RootLoader(lc);
            }
        });
        Method m = null;
        try {
            Class<?> c = loader.loadClass(lc.getMainClass());
            m = c.getMethod("main", String[].class);
        }
        catch (ClassNotFoundException e1) {
            GroovyStarter.exit(e1);
        }
        catch (SecurityException e2) {
            GroovyStarter.exit(e2);
        }
        catch (NoSuchMethodException e2) {
            GroovyStarter.exit(e2);
        }
        try {
            m.invoke(null, new Object[]{newArgs});
        }
        catch (IllegalArgumentException e3) {
            GroovyStarter.exit(e3);
        }
        catch (IllegalAccessException e3) {
            GroovyStarter.exit(e3);
        }
        catch (InvocationTargetException e3) {
            GroovyStarter.exit(e3);
        }
    }

    private static void exit(Exception e) {
        e.printStackTrace();
        System.exit(1);
    }

    private static void exit(String msg) {
        System.err.println(msg);
        System.exit(1);
    }

    public static void main(String[] args) {
        try {
            GroovyStarter.rootLoader(args);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
}

