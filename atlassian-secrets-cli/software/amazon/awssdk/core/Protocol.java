/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core;

import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public enum Protocol {
    HTTP("http"),
    HTTPS("https");

    private final String protocol;

    private Protocol(String protocol) {
        this.protocol = protocol;
    }

    public String toString() {
        return this.protocol;
    }
}

