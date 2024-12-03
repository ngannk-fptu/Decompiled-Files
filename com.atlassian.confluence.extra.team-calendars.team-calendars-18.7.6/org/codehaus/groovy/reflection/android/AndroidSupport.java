/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection.android;

public abstract class AndroidSupport {
    private static final boolean IS_ANDROID;

    public static boolean isRunningAndroid() {
        return IS_ANDROID;
    }

    static {
        boolean isAndroid = true;
        try {
            Class.forName("android.app.Activity", false, AndroidSupport.class.getClassLoader());
        }
        catch (ClassNotFoundException e) {
            isAndroid = false;
        }
        IS_ANDROID = isAndroid;
    }
}

