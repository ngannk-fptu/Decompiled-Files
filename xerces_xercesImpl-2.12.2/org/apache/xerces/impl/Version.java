/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl;

public class Version {
    public static String fVersion = "Xerces-J 2.12.2";
    private static final String fImmutableVersion = "Xerces-J 2.12.2";

    public static String getVersion() {
        return fImmutableVersion;
    }

    public static void main(String[] stringArray) {
        System.out.println(fVersion);
    }
}

