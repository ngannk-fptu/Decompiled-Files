/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1BitString
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.DERBitString
 *  org.bouncycastle.asn1.crmf.EncryptedValue
 *  org.bouncycastle.asn1.pkcs.PrivateKeyInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Strings
 */
package org.bouncycastle.cert.crmf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.crmf.EncryptedValue;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.EncryptedValuePadder;
import org.bouncycastle.operator.KeyWrapper;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfoBuilder;
import org.bouncycastle.util.Strings;

public class EncryptedValueBuilder {
    private KeyWrapper wrapper;
    private OutputEncryptor encryptor;
    private EncryptedValuePadder padder;

    public EncryptedValueBuilder(KeyWrapper wrapper, OutputEncryptor encryptor) {
        this(wrapper, encryptor, null);
    }

    public EncryptedValueBuilder(KeyWrapper wrapper, OutputEncryptor encryptor, EncryptedValuePadder padder) {
        this.wrapper = wrapper;
        this.encryptor = encryptor;
        this.padder = padder;
    }

    public EncryptedValue build(char[] revocationPassphrase) throws CRMFException {
        return this.encryptData(this.padData(Strings.toUTF8ByteArray((char[])revocationPassphrase)));
    }

    public EncryptedValue build(X509CertificateHolder holder) throws CRMFException {
        try {
            return this.encryptData(this.padData(holder.getEncoded()));
        }
        catch (IOException e) {
            throw new CRMFException("cannot encode certificate: " + e.getMessage(), e);
        }
    }

    public EncryptedValue build(PrivateKeyInfo privateKeyInfo) throws CRMFException {
        PKCS8EncryptedPrivateKeyInfoBuilder encInfoBldr = new PKCS8EncryptedPrivateKeyInfoBuilder(privateKeyInfo);
        AlgorithmIdentifier intendedAlg = privateKeyInfo.getPrivateKeyAlgorithm();
        AlgorithmIdentifier symmAlg = this.encryptor.getAlgorithmIdentifier();
        try {
            PKCS8EncryptedPrivateKeyInfo encInfo = encInfoBldr.build(this.encryptor);
            DERBitString encSymmKey = new DERBitString(this.wrapper.generateWrappedKey(this.encryptor.getKey()));
            AlgorithmIdentifier keyAlg = this.wrapper.getAlgorithmIdentifier();
            ASN1OctetString valueHint = null;
            return new EncryptedValue(intendedAlg, symmAlg, (ASN1BitString)encSymmKey, keyAlg, valueHint, (ASN1BitString)new DERBitString(encInfo.getEncryptedData()));
        }
        catch (IllegalStateException e) {
            throw new CRMFException("cannot encode key: " + e.getMessage(), e);
        }
        catch (OperatorException e) {
            throw new CRMFException("cannot wrap key: " + e.getMessage(), e);
        }
    }

    private EncryptedValue encryptData(byte[] data) throws CRMFException {
        DERBitString encSymmKey;
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        OutputStream eOut = this.encryptor.getOutputStream(bOut);
        try {
            eOut.write(data);
            eOut.close();
        }
        catch (IOException e) {
            throw new CRMFException("cannot process data: " + e.getMessage(), e);
        }
        AlgorithmIdentifier intendedAlg = null;
        AlgorithmIdentifier symmAlg = this.encryptor.getAlgorithmIdentifier();
        try {
            this.wrapper.generateWrappedKey(this.encryptor.getKey());
            encSymmKey = new DERBitString(this.wrapper.generateWrappedKey(this.encryptor.getKey()));
        }
        catch (OperatorException e) {
            throw new CRMFException("cannot wrap key: " + e.getMessage(), e);
        }
        AlgorithmIdentifier keyAlg = this.wrapper.getAlgorithmIdentifier();
        ASN1OctetString valueHint = null;
        DERBitString encValue = new DERBitString(bOut.toByteArray());
        return new EncryptedValue(intendedAlg, symmAlg, (ASN1BitString)encSymmKey, keyAlg, valueHint, (ASN1BitString)encValue);
    }

    private byte[] padData(byte[] data) {
        if (this.padder != null) {
            return this.padder.getPaddedData(data);
        }
        return data;
    }
}

