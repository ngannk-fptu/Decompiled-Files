/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.pool;

public enum PoolExhaustedAction {
    FAIL(0),
    BLOCK(1),
    GROW(2);

    private final byte value;

    private PoolExhaustedAction(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return this.value;
    }
}

