/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.crypto.keygen;

public interface BytesKeyGenerator {
    public int getKeyLength();

    public byte[] generateKey();
}

