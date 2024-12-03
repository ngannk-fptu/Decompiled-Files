/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.ssl;

public enum ApplicationProtocol {
    HTTP_2("h2"),
    HTTP_1_1("http/1.1");

    public final String id;

    private ApplicationProtocol(String id) {
        this.id = id;
    }

    public String toString() {
        return this.id;
    }
}

