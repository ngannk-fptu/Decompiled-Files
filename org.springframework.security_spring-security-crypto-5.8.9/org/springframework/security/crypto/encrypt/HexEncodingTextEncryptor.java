/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.crypto.encrypt;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.TextEncryptor;

final class HexEncodingTextEncryptor
implements TextEncryptor {
    private final BytesEncryptor encryptor;

    HexEncodingTextEncryptor(BytesEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    @Override
    public String encrypt(String text) {
        return new String(Hex.encode(this.encryptor.encrypt(Utf8.encode(text))));
    }

    @Override
    public String decrypt(String encryptedText) {
        return Utf8.decode(this.encryptor.decrypt(Hex.decode(encryptedText)));
    }
}

