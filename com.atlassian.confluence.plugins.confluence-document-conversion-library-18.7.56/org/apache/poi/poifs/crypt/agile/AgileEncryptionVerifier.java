/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.crypt.agile;

import java.util.Iterator;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.poifs.crypt.EncryptionVerifier;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.agile.AgileEncryptionInfoBuilder;
import org.apache.poi.poifs.crypt.agile.EncryptionDocument;
import org.apache.poi.poifs.crypt.agile.KeyEncryptor;
import org.apache.poi.poifs.crypt.agile.PasswordKeyEncryptor;

public class AgileEncryptionVerifier
extends EncryptionVerifier {
    private int keyBits = -1;
    private int blockSize = -1;

    public AgileEncryptionVerifier(String descriptor) {
        this(AgileEncryptionInfoBuilder.parseDescriptor(descriptor));
    }

    protected AgileEncryptionVerifier(EncryptionDocument ed) {
        KeyEncryptor ke;
        PasswordKeyEncryptor keyData = null;
        Iterator<KeyEncryptor> iterator = ed.getKeyEncryptors().iterator();
        while (iterator.hasNext() && (keyData = (ke = iterator.next()).getPasswordKeyEncryptor()) == null) {
        }
        if (keyData == null) {
            throw new IllegalArgumentException("encryptedKey not set");
        }
        this.setCipherAlgorithm(keyData.getCipherAlgorithm());
        this.setKeySize(keyData.getKeyBits());
        int blockSize = keyData.getBlockSize();
        this.setBlockSize(blockSize);
        int hashSize = keyData.getHashSize();
        HashAlgorithm ha = keyData.getHashAlgorithm();
        this.setHashAlgorithm(ha);
        if (this.getHashAlgorithm().hashSize != hashSize) {
            throw new EncryptedDocumentException("Unsupported hash algorithm: " + (Object)((Object)keyData.getHashAlgorithm()) + " @ " + hashSize + " bytes");
        }
        Integer spinCount = keyData.getSpinCount();
        if (spinCount != null) {
            this.setSpinCount(spinCount);
        }
        this.setEncryptedVerifier(keyData.getEncryptedVerifierHashInput());
        this.setSalt(keyData.getSaltValue());
        this.setEncryptedKey(keyData.getEncryptedKeyValue());
        this.setEncryptedVerifierHash(keyData.getEncryptedVerifierHashValue());
        Integer saltSize = keyData.getSaltSize();
        if (saltSize == null || saltSize != this.getSalt().length) {
            throw new EncryptedDocumentException("Invalid salt size");
        }
        this.setChainingMode(keyData.getCipherChaining());
        if (keyData.getCipherChaining() != ChainingMode.cbc && keyData.getCipherChaining() != ChainingMode.cfb) {
            throw new EncryptedDocumentException("Unsupported chaining mode - " + (Object)((Object)keyData.getCipherChaining()));
        }
    }

    public AgileEncryptionVerifier(CipherAlgorithm cipherAlgorithm, HashAlgorithm hashAlgorithm, int keyBits, int blockSize, ChainingMode chainingMode) {
        this.setCipherAlgorithm(cipherAlgorithm);
        this.setHashAlgorithm(hashAlgorithm);
        this.setChainingMode(chainingMode);
        this.setKeySize(keyBits);
        this.setBlockSize(blockSize);
        this.setSpinCount(100000);
    }

    public AgileEncryptionVerifier(AgileEncryptionVerifier other) {
        super(other);
        this.keyBits = other.keyBits;
        this.blockSize = other.blockSize;
    }

    @Override
    public void setSalt(byte[] salt) {
        if (salt == null || salt.length != this.getCipherAlgorithm().blockSize) {
            throw new EncryptedDocumentException("invalid verifier salt");
        }
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
    public void setEncryptedKey(byte[] encryptedKey) {
        super.setEncryptedKey(encryptedKey);
    }

    @Override
    public AgileEncryptionVerifier copy() {
        return new AgileEncryptionVerifier(this);
    }

    public int getKeySize() {
        return this.keyBits;
    }

    public int getBlockSize() {
        return this.blockSize;
    }

    public void setKeySize(int keyBits) {
        this.keyBits = keyBits;
        for (int allowedBits : this.getCipherAlgorithm().allowedKeySize) {
            if (allowedBits != keyBits) continue;
            return;
        }
        throw new EncryptedDocumentException("KeySize " + keyBits + " not allowed for cipher " + (Object)((Object)this.getCipherAlgorithm()));
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    @Override
    public final void setCipherAlgorithm(CipherAlgorithm cipherAlgorithm) {
        super.setCipherAlgorithm(cipherAlgorithm);
        if (cipherAlgorithm.allowedKeySize.length == 1) {
            this.setKeySize(cipherAlgorithm.defaultKeySize);
        }
    }
}

