/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.rp;

public enum ApplicationType {
    NATIVE,
    WEB;


    public static ApplicationType getDefault() {
        return WEB;
    }

    public String toString() {
        return super.toString().toLowerCase();
    }
}

