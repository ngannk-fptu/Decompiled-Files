/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.net;

@Deprecated
public class ContainerThreadMarker {
    private static final ThreadLocal<Boolean> marker = new ThreadLocal();

    public static boolean isContainerThread() {
        Boolean flag = marker.get();
        if (flag == null) {
            return false;
        }
        return flag;
    }

    public static void set() {
        marker.set(Boolean.TRUE);
    }

    public static void clear() {
        marker.set(Boolean.FALSE);
    }
}

