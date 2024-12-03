/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util;

public class VmUtils {
    private static boolean inGoogleAppEngine;

    public static boolean isInGoogleAppEngine() {
        return inGoogleAppEngine;
    }

    static {
        try {
            Class.forName("com.google.apphosting.api.DeadlineExceededException");
            inGoogleAppEngine = true;
        }
        catch (ClassNotFoundException cnfe) {
            inGoogleAppEngine = false;
        }
    }
}

