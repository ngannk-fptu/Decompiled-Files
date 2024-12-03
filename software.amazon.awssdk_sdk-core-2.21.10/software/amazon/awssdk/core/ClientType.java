/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.core;

import software.amazon.awssdk.annotations.SdkPublicApi;

@SdkPublicApi
public enum ClientType {
    ASYNC("Async"),
    SYNC("Sync"),
    UNKNOWN("Unknown");

    private final String clientType;

    private ClientType(String clientType) {
        this.clientType = clientType;
    }

    public String toString() {
        return this.clientType;
    }
}

