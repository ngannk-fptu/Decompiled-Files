/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.driver.textui;

public class Debug {
    public static boolean debug;

    static {
        try {
            debug = System.getProperty("com.ctc.wstx.shaded.msv_core.debug") != null;
        }
        catch (SecurityException e) {
            debug = false;
        }
    }
}

