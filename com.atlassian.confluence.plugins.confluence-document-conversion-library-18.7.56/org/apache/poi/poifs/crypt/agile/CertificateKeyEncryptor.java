/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.crypt.agile;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.agile.EncryptionDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CertificateKeyEncryptor {
    private byte[] encryptedKeyValue;
    private byte[] x509Certificate;
    private byte[] certVerifier;

    public CertificateKeyEncryptor(Element certificateKey) {
        if (certificateKey == null) {
            throw new EncryptedDocumentException("Unable to parse encryption descriptor");
        }
        this.encryptedKeyValue = EncryptionDocument.getBinAttr(certificateKey, "encryptedKeyValue");
        this.x509Certificate = EncryptionDocument.getBinAttr(certificateKey, "X509Certificate");
        this.certVerifier = EncryptionDocument.getBinAttr(certificateKey, "certVerifier");
    }

    void write(Element encryption) {
        Document doc = encryption.getOwnerDocument();
        Element keyEncryptor = (Element)encryption.appendChild(doc.createElementNS("http://schemas.microsoft.com/office/2006/encryption", "keyEncryptor"));
        keyEncryptor.setAttribute("uri", "http://schemas.microsoft.com/office/2006/keyEncryptor/certificate");
        Element encryptedKey = (Element)keyEncryptor.appendChild(doc.createElementNS("http://schemas.microsoft.com/office/2006/keyEncryptor/certificate", "c:encryptedKey"));
        EncryptionDocument.setBinAttr(encryptedKey, "encryptedKeyValue", this.encryptedKeyValue);
        EncryptionDocument.setBinAttr(encryptedKey, "x509Certificate", this.x509Certificate);
        EncryptionDocument.setBinAttr(encryptedKey, "certVerifier", this.certVerifier);
    }

    public byte[] getEncryptedKeyValue() {
        return this.encryptedKeyValue;
    }

    public void setEncryptedKeyValue(byte[] encryptedKeyValue) {
        this.encryptedKeyValue = encryptedKeyValue;
    }

    public byte[] getX509Certificate() {
        return this.x509Certificate;
    }

    public void setX509Certificate(byte[] x509Certificate) {
        this.x509Certificate = x509Certificate;
    }

    public byte[] getCertVerifier() {
        return this.certVerifier;
    }

    public void setCertVerifier(byte[] certVerifier) {
        this.certVerifier = certVerifier;
    }
}

