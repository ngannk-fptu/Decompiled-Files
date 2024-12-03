/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp;

public enum Stage {
    START,
    EXECUTE;


    public static Stage lookup(String name) {
        if ("execute".equalsIgnoreCase(name)) {
            return EXECUTE;
        }
        return START;
    }
}

