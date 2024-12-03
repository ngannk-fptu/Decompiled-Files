/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.cms.IssuerAndSerialNumber
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.cms.jcajce;

import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.KeyTransRecipientInfoGenerator;
import org.bouncycastle.operator.AsymmetricKeyWrapper;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyWrapper;

public class JceKeyTransRecipientInfoGenerator
extends KeyTransRecipientInfoGenerator {
    public JceKeyTransRecipientInfoGenerator(X509Certificate recipientCert) throws CertificateEncodingException {
        super(new IssuerAndSerialNumber(new JcaX509CertificateHolder(recipientCert).toASN1Structure()), (AsymmetricKeyWrapper)new JceAsymmetricKeyWrapper(recipientCert));
    }

    public JceKeyTransRecipientInfoGenerator(X509Certificate recipientCert, AsymmetricKeyWrapper wrapper) throws CertificateEncodingException {
        super(new IssuerAndSerialNumber(new JcaX509CertificateHolder(recipientCert).toASN1Structure()), wrapper);
    }

    public JceKeyTransRecipientInfoGenerator(byte[] subjectKeyIdentifier, PublicKey publicKey) {
        super(subjectKeyIdentifier, (AsymmetricKeyWrapper)new JceAsymmetricKeyWrapper(publicKey));
    }

    public JceKeyTransRecipientInfoGenerator(byte[] subjectKeyIdentifier, AsymmetricKeyWrapper wrapper) {
        super(subjectKeyIdentifier, wrapper);
    }

    public JceKeyTransRecipientInfoGenerator(X509Certificate recipientCert, AlgorithmIdentifier algorithmIdentifier) throws CertificateEncodingException {
        super(new IssuerAndSerialNumber(new JcaX509CertificateHolder(recipientCert).toASN1Structure()), (AsymmetricKeyWrapper)new JceAsymmetricKeyWrapper(algorithmIdentifier, recipientCert.getPublicKey()));
    }

    public JceKeyTransRecipientInfoGenerator(byte[] subjectKeyIdentifier, AlgorithmIdentifier algorithmIdentifier, PublicKey publicKey) {
        super(subjectKeyIdentifier, (AsymmetricKeyWrapper)new JceAsymmetricKeyWrapper(algorithmIdentifier, publicKey));
    }

    public JceKeyTransRecipientInfoGenerator setProvider(String providerName) {
        ((JceAsymmetricKeyWrapper)this.wrapper).setProvider(providerName);
        return this;
    }

    public JceKeyTransRecipientInfoGenerator setProvider(Provider provider) {
        ((JceAsymmetricKeyWrapper)this.wrapper).setProvider(provider);
        return this;
    }

    public JceKeyTransRecipientInfoGenerator setAlgorithmMapping(ASN1ObjectIdentifier algorithm, String algorithmName) {
        ((JceAsymmetricKeyWrapper)this.wrapper).setAlgorithmMapping(algorithm, algorithmName);
        return this;
    }
}

