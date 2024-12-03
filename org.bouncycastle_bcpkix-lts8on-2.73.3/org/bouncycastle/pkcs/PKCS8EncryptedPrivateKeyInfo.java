/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo
 *  org.bouncycastle.asn1.pkcs.PrivateKeyInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.io.Streams
 */
package org.bouncycastle.pkcs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.PKCSIOException;
import org.bouncycastle.util.io.Streams;

public class PKCS8EncryptedPrivateKeyInfo {
    private EncryptedPrivateKeyInfo encryptedPrivateKeyInfo;

    private static EncryptedPrivateKeyInfo parseBytes(byte[] pkcs8Encoding) throws IOException {
        try {
            return EncryptedPrivateKeyInfo.getInstance((Object)ASN1Primitive.fromByteArray((byte[])pkcs8Encoding));
        }
        catch (ClassCastException e) {
            throw new PKCSIOException("malformed data: " + e.getMessage(), e);
        }
        catch (IllegalArgumentException e) {
            throw new PKCSIOException("malformed data: " + e.getMessage(), e);
        }
    }

    public PKCS8EncryptedPrivateKeyInfo(EncryptedPrivateKeyInfo encryptedPrivateKeyInfo) {
        this.encryptedPrivateKeyInfo = encryptedPrivateKeyInfo;
    }

    public PKCS8EncryptedPrivateKeyInfo(byte[] encryptedPrivateKeyInfo) throws IOException {
        this(PKCS8EncryptedPrivateKeyInfo.parseBytes(encryptedPrivateKeyInfo));
    }

    public AlgorithmIdentifier getEncryptionAlgorithm() {
        return this.encryptedPrivateKeyInfo.getEncryptionAlgorithm();
    }

    public byte[] getEncryptedData() {
        return this.encryptedPrivateKeyInfo.getEncryptedData();
    }

    public EncryptedPrivateKeyInfo toASN1Structure() {
        return this.encryptedPrivateKeyInfo;
    }

    public byte[] getEncoded() throws IOException {
        return this.encryptedPrivateKeyInfo.getEncoded();
    }

    public PrivateKeyInfo decryptPrivateKeyInfo(InputDecryptorProvider inputDecryptorProvider) throws PKCSException {
        try {
            InputDecryptor decrytor = inputDecryptorProvider.get(this.encryptedPrivateKeyInfo.getEncryptionAlgorithm());
            ByteArrayInputStream encIn = new ByteArrayInputStream(this.encryptedPrivateKeyInfo.getEncryptedData());
            return PrivateKeyInfo.getInstance((Object)Streams.readAll((InputStream)decrytor.getInputStream(encIn)));
        }
        catch (Exception e) {
            throw new PKCSException("unable to read encrypted data: " + e.getMessage(), e);
        }
    }
}

