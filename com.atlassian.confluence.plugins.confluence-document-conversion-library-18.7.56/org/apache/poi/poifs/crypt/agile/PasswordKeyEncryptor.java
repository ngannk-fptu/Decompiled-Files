/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.crypt.agile;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.agile.EncryptionDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PasswordKeyEncryptor {
    private Integer saltSize;
    private Integer blockSize;
    private Integer keyBits;
    private Integer hashSize;
    private CipherAlgorithm cipherAlgorithm;
    private ChainingMode cipherChaining;
    private HashAlgorithm hashAlgorithm;
    private byte[] saltValue;
    private Integer spinCount;
    private byte[] encryptedVerifierHashInput;
    private byte[] encryptedVerifierHashValue;
    private byte[] encryptedKeyValue;

    public PasswordKeyEncryptor() {
    }

    public PasswordKeyEncryptor(Element passwordKey) {
        if (passwordKey == null) {
            throw new EncryptedDocumentException("Unable to parse encryption descriptor");
        }
        this.saltSize = EncryptionDocument.getIntAttr(passwordKey, "saltSize");
        this.blockSize = EncryptionDocument.getIntAttr(passwordKey, "blockSize");
        this.keyBits = EncryptionDocument.getIntAttr(passwordKey, "keyBits");
        this.hashSize = EncryptionDocument.getIntAttr(passwordKey, "hashSize");
        this.cipherAlgorithm = CipherAlgorithm.fromXmlId(passwordKey.getAttribute("cipherAlgorithm"), this.keyBits);
        this.cipherChaining = ChainingMode.fromXmlId(passwordKey.getAttribute("cipherChaining"));
        this.hashAlgorithm = HashAlgorithm.fromEcmaId(passwordKey.getAttribute("hashAlgorithm"));
        this.saltValue = EncryptionDocument.getBinAttr(passwordKey, "saltValue");
        this.spinCount = EncryptionDocument.getIntAttr(passwordKey, "spinCount");
        this.encryptedVerifierHashInput = EncryptionDocument.getBinAttr(passwordKey, "encryptedVerifierHashInput");
        this.encryptedVerifierHashValue = EncryptionDocument.getBinAttr(passwordKey, "encryptedVerifierHashValue");
        this.encryptedKeyValue = EncryptionDocument.getBinAttr(passwordKey, "encryptedKeyValue");
    }

    void write(Element encryption) {
        Document doc = encryption.getOwnerDocument();
        Element keyEncryptor = (Element)encryption.appendChild(doc.createElementNS("http://schemas.microsoft.com/office/2006/encryption", "keyEncryptor"));
        keyEncryptor.setAttribute("uri", "http://schemas.microsoft.com/office/2006/keyEncryptor/password");
        Element encryptedKey = (Element)keyEncryptor.appendChild(doc.createElementNS("http://schemas.microsoft.com/office/2006/keyEncryptor/password", "p:encryptedKey"));
        EncryptionDocument.setIntAttr(encryptedKey, "saltSize", this.saltSize);
        EncryptionDocument.setIntAttr(encryptedKey, "blockSize", this.blockSize);
        EncryptionDocument.setIntAttr(encryptedKey, "keyBits", this.keyBits);
        EncryptionDocument.setIntAttr(encryptedKey, "hashSize", this.hashSize);
        EncryptionDocument.setAttr(encryptedKey, "cipherAlgorithm", this.cipherAlgorithm == null ? null : this.cipherAlgorithm.xmlId);
        EncryptionDocument.setAttr(encryptedKey, "cipherChaining", this.cipherChaining == null ? null : this.cipherChaining.xmlId);
        EncryptionDocument.setAttr(encryptedKey, "hashAlgorithm", this.hashAlgorithm == null ? null : this.hashAlgorithm.ecmaString);
        EncryptionDocument.setBinAttr(encryptedKey, "saltValue", this.saltValue);
        EncryptionDocument.setIntAttr(encryptedKey, "spinCount", this.spinCount);
        EncryptionDocument.setBinAttr(encryptedKey, "encryptedVerifierHashInput", this.encryptedVerifierHashInput);
        EncryptionDocument.setBinAttr(encryptedKey, "encryptedVerifierHashValue", this.encryptedVerifierHashValue);
        EncryptionDocument.setBinAttr(encryptedKey, "encryptedKeyValue", this.encryptedKeyValue);
    }

    public Integer getSaltSize() {
        return this.saltSize;
    }

    public void setSaltSize(Integer saltSize) {
        this.saltSize = saltSize;
    }

    public Integer getBlockSize() {
        return this.blockSize;
    }

    public void setBlockSize(Integer blockSize) {
        this.blockSize = blockSize;
    }

    public Integer getKeyBits() {
        return this.keyBits;
    }

    public void setKeyBits(Integer keyBits) {
        this.keyBits = keyBits;
    }

    public Integer getHashSize() {
        return this.hashSize;
    }

    public void setHashSize(Integer hashSize) {
        this.hashSize = hashSize;
    }

    public CipherAlgorithm getCipherAlgorithm() {
        return this.cipherAlgorithm;
    }

    public void setCipherAlgorithm(CipherAlgorithm cipherAlgorithm) {
        this.cipherAlgorithm = cipherAlgorithm;
    }

    public ChainingMode getCipherChaining() {
        return this.cipherChaining;
    }

    public void setCipherChaining(ChainingMode cipherChaining) {
        this.cipherChaining = cipherChaining;
    }

    public HashAlgorithm getHashAlgorithm() {
        return this.hashAlgorithm;
    }

    public void setHashAlgorithm(HashAlgorithm hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public byte[] getSaltValue() {
        return this.saltValue;
    }

    public void setSaltValue(byte[] saltValue) {
        this.saltValue = saltValue;
    }

    public Integer getSpinCount() {
        return this.spinCount;
    }

    public void setSpinCount(Integer spinCount) {
        this.spinCount = spinCount;
    }

    public byte[] getEncryptedVerifierHashInput() {
        return this.encryptedVerifierHashInput;
    }

    public void setEncryptedVerifierHashInput(byte[] encryptedVerifierHashInput) {
        this.encryptedVerifierHashInput = encryptedVerifierHashInput;
    }

    public byte[] getEncryptedVerifierHashValue() {
        return this.encryptedVerifierHashValue;
    }

    public void setEncryptedVerifierHashValue(byte[] encryptedVerifierHashValue) {
        this.encryptedVerifierHashValue = encryptedVerifierHashValue;
    }

    public byte[] getEncryptedKeyValue() {
        return this.encryptedKeyValue;
    }

    public void setEncryptedKeyValue(byte[] encryptedKeyValue) {
        this.encryptedKeyValue = encryptedKeyValue;
    }
}

