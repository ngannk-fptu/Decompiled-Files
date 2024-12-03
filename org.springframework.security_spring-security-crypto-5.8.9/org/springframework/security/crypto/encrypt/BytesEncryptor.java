/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.crypto.encrypt;

public interface BytesEncryptor {
    public byte[] encrypt(byte[] var1);

    public byte[] decrypt(byte[] var1);
}

