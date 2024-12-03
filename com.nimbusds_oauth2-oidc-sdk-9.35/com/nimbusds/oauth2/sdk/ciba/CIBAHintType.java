/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.ciba;

public enum CIBAHintType {
    LOGIN_HINT_TOKEN,
    ID_TOKEN_HINT,
    LOGIN_HINT;


    public String toString() {
        return super.toString().toLowerCase();
    }
}

