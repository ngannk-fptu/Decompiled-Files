/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote;

@Deprecated
public class ContainerThreadMarker {
    public static boolean isContainerThread() {
        return org.apache.tomcat.util.net.ContainerThreadMarker.isContainerThread();
    }

    public static void set() {
        org.apache.tomcat.util.net.ContainerThreadMarker.set();
    }

    public static void clear() {
        org.apache.tomcat.util.net.ContainerThreadMarker.clear();
    }
}

