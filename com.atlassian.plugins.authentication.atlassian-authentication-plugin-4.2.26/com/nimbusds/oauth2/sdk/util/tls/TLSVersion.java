/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.util.tls;

public enum TLSVersion {
    TLS("TLS"),
    TLS_1("TLSv1"),
    TLS_1_1("TLSv1.1"),
    TLS_1_2("TLSv1.2"),
    TLS_1_3("TLSv1.3");

    private final String value;

    private TLSVersion(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }
}

