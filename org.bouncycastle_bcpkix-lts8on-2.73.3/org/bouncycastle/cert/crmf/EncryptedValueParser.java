/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.crmf.EncryptedValue
 *  org.bouncycastle.asn1.pkcs.PrivateKeyInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.Certificate
 *  org.bouncycastle.util.Strings
 *  org.bouncycastle.util.io.Streams
 */
package org.bouncycastle.cert.crmf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.crmf.EncryptedValue;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.EncryptedValuePadder;
import org.bouncycastle.cert.crmf.ValueDecryptorGenerator;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.io.Streams;

public class EncryptedValueParser {
    private EncryptedValue value;
    private EncryptedValuePadder padder;

    public EncryptedValueParser(EncryptedValue value) {
        this.value = value;
    }

    public EncryptedValueParser(EncryptedValue value, EncryptedValuePadder padder) {
        this.value = value;
        this.padder = padder;
    }

    public AlgorithmIdentifier getIntendedAlg() {
        return this.value.getIntendedAlg();
    }

    private byte[] decryptValue(ValueDecryptorGenerator decGen) throws CRMFException {
        if (this.value.getValueHint() != null) {
            throw new UnsupportedOperationException();
        }
        InputDecryptor decryptor = decGen.getValueDecryptor(this.value.getKeyAlg(), this.value.getSymmAlg(), this.value.getEncSymmKey().getBytes());
        InputStream dataIn = decryptor.getInputStream(new ByteArrayInputStream(this.value.getEncValue().getBytes()));
        try {
            return this.unpadData(Streams.readAll((InputStream)dataIn));
        }
        catch (IOException e) {
            throw new CRMFException("Cannot parse decrypted data: " + e.getMessage(), e);
        }
    }

    public X509CertificateHolder readCertificateHolder(ValueDecryptorGenerator decGen) throws CRMFException {
        return new X509CertificateHolder(Certificate.getInstance((Object)this.decryptValue(decGen)));
    }

    public PrivateKeyInfo readPrivateKeyInfo(ValueDecryptorGenerator decGen) throws CRMFException {
        return PrivateKeyInfo.getInstance((Object)this.decryptValue(decGen));
    }

    public char[] readPassphrase(ValueDecryptorGenerator decGen) throws CRMFException {
        return Strings.fromUTF8ByteArray((byte[])this.decryptValue(decGen)).toCharArray();
    }

    private byte[] unpadData(byte[] data) {
        if (this.padder != null) {
            return this.padder.getUnpaddedData(data);
        }
        return data;
    }
}

