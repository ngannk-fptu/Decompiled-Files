/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol;

public enum AuthenticationStatus {
    AUTHENTICATED(0),
    CREDENTIALS_FAILED(1),
    SERIALIZATION_VERSION_MISMATCH(2),
    NOT_ALLOWED_IN_CLUSTER(3);

    private final byte id;

    private AuthenticationStatus(int status) {
        this.id = (byte)status;
    }

    public byte getId() {
        return this.id;
    }

    public static AuthenticationStatus getById(int id) {
        for (AuthenticationStatus as : AuthenticationStatus.values()) {
            if (as.getId() != id) continue;
            return as;
        }
        throw new IllegalArgumentException("Unsupported ID value");
    }
}

