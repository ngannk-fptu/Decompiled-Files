/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.crypt.cryptoapi;

import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.cryptoapi.CryptoAPIEncryptionHeader;
import org.apache.poi.poifs.crypt.standard.StandardEncryptionVerifier;
import org.apache.poi.util.LittleEndianInput;

public class CryptoAPIEncryptionVerifier
extends StandardEncryptionVerifier {
    protected CryptoAPIEncryptionVerifier(LittleEndianInput is, CryptoAPIEncryptionHeader header) {
        super(is, header);
    }

    protected CryptoAPIEncryptionVerifier(CipherAlgorithm cipherAlgorithm, HashAlgorithm hashAlgorithm, int keyBits, int blockSize, ChainingMode chainingMode) {
        super(cipherAlgorithm, hashAlgorithm, keyBits, blockSize, chainingMode);
    }

    protected CryptoAPIEncryptionVerifier(CryptoAPIEncryptionVerifier other) {
        super(other);
    }

    @Override
    public void setSalt(byte[] salt) {
        super.setSalt(salt);
    }

    @Override
    public void setEncryptedVerifier(byte[] encryptedVerifier) {
        super.setEncryptedVerifier(encryptedVerifier);
    }

    @Override
    public void setEncryptedVerifierHash(byte[] encryptedVerifierHash) {
        super.setEncryptedVerifierHash(encryptedVerifierHash);
    }

    @Override
    public CryptoAPIEncryptionVerifier copy() {
        return new CryptoAPIEncryptionVerifier(this);
    }
}

