/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.base;

import java.net.URL;
import java.util.Enumeration;
import org.jfree.util.ObjectUtilities;

public class ClassPathDebugger {
    static /* synthetic */ Class class$org$jfree$util$ObjectUtilities;
    static /* synthetic */ Class class$java$lang$String;

    public static void main(String[] args) {
        System.out.println("Listing the various classloaders:");
        System.out.println("Defined classloader source: " + ObjectUtilities.getClassLoaderSource());
        System.out.println("User classloader: " + ObjectUtilities.getClassLoader());
        System.out.println("Classloader for ObjectUtilities.class: " + ObjectUtilities.getClassLoader(class$org$jfree$util$ObjectUtilities == null ? (class$org$jfree$util$ObjectUtilities = ClassPathDebugger.class$("org.jfree.util.ObjectUtilities")) : class$org$jfree$util$ObjectUtilities));
        System.out.println("Classloader for String.class: " + ObjectUtilities.getClassLoader(class$java$lang$String == null ? (class$java$lang$String = ClassPathDebugger.class$("java.lang.String")) : class$java$lang$String));
        System.out.println("Thread-Context Classloader: " + Thread.currentThread().getContextClassLoader());
        System.out.println("Defined System classloader: " + ClassLoader.getSystemClassLoader());
        System.out.println();
        try {
            System.out.println("Listing sources for '/jcommon.properties':");
            Enumeration<URL> resources = ObjectUtilities.getClassLoader(class$org$jfree$util$ObjectUtilities == null ? (class$org$jfree$util$ObjectUtilities = ClassPathDebugger.class$("org.jfree.util.ObjectUtilities")) : class$org$jfree$util$ObjectUtilities).getResources("jcommon.properties");
            while (resources.hasMoreElements()) {
                System.out.println(" " + resources.nextElement());
            }
            System.out.println();
            System.out.println("Listing sources for 'org/jfree/JCommonInfo.class':");
            resources = ObjectUtilities.getClassLoader(class$org$jfree$util$ObjectUtilities == null ? (class$org$jfree$util$ObjectUtilities = ClassPathDebugger.class$("org.jfree.util.ObjectUtilities")) : class$org$jfree$util$ObjectUtilities).getResources("org/jfree/JCommonInfo.class");
            while (resources.hasMoreElements()) {
                System.out.println(" " + resources.nextElement());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

