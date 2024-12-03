/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.claims;

public enum ClaimsTransport {
    USERINFO,
    ID_TOKEN;


    public static ClaimsTransport getDefault() {
        return USERINFO;
    }
}

