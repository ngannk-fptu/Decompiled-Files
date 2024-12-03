/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.cms.IssuerAndSerialNumber
 *  org.bouncycastle.asn1.cms.SignerIdentifier
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.SignerIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.CMSSignatureEncryptionAlgorithmFinder;
import org.bouncycastle.cms.DefaultCMSSignatureEncryptionAlgorithmFinder;
import org.bouncycastle.cms.DefaultSignedAttributeTableGenerator;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

public class SignerInfoGeneratorBuilder {
    private final DigestAlgorithmIdentifierFinder digAlgFinder = new DefaultDigestAlgorithmIdentifierFinder();
    private DigestCalculatorProvider digestProvider;
    private boolean directSignature;
    private CMSAttributeTableGenerator signedGen;
    private CMSAttributeTableGenerator unsignedGen;
    private CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder;
    private AlgorithmIdentifier contentDigest;

    public SignerInfoGeneratorBuilder(DigestCalculatorProvider digestProvider) {
        this(digestProvider, new DefaultCMSSignatureEncryptionAlgorithmFinder());
    }

    public SignerInfoGeneratorBuilder(DigestCalculatorProvider digestProvider, CMSSignatureEncryptionAlgorithmFinder sigEncAlgFinder) {
        this.digestProvider = digestProvider;
        this.sigEncAlgFinder = sigEncAlgFinder;
    }

    public SignerInfoGeneratorBuilder setDirectSignature(boolean hasNoSignedAttributes) {
        this.directSignature = hasNoSignedAttributes;
        return this;
    }

    public SignerInfoGeneratorBuilder setContentDigest(AlgorithmIdentifier contentDigest) {
        this.contentDigest = contentDigest;
        return this;
    }

    public SignerInfoGeneratorBuilder setSignedAttributeGenerator(CMSAttributeTableGenerator signedGen) {
        this.signedGen = signedGen;
        return this;
    }

    public SignerInfoGeneratorBuilder setUnsignedAttributeGenerator(CMSAttributeTableGenerator unsignedGen) {
        this.unsignedGen = unsignedGen;
        return this;
    }

    public SignerInfoGenerator build(ContentSigner contentSigner, X509CertificateHolder certHolder) throws OperatorCreationException {
        SignerIdentifier sigId = new SignerIdentifier(new IssuerAndSerialNumber(certHolder.toASN1Structure()));
        SignerInfoGenerator sigInfoGen = this.createGenerator(contentSigner, sigId);
        sigInfoGen.setAssociatedCertificate(certHolder);
        return sigInfoGen;
    }

    public SignerInfoGenerator build(ContentSigner contentSigner, byte[] subjectKeyIdentifier) throws OperatorCreationException {
        SignerIdentifier sigId = new SignerIdentifier((ASN1OctetString)new DEROctetString(subjectKeyIdentifier));
        return this.createGenerator(contentSigner, sigId);
    }

    private SignerInfoGenerator createGenerator(ContentSigner contentSigner, SignerIdentifier sigId) throws OperatorCreationException {
        DigestCalculator digester = this.contentDigest != null ? this.digestProvider.get(this.contentDigest) : this.digestProvider.get(this.digAlgFinder.find(contentSigner.getAlgorithmIdentifier()));
        if (this.directSignature) {
            return new SignerInfoGenerator(sigId, contentSigner, digester.getAlgorithmIdentifier(), this.sigEncAlgFinder);
        }
        if (this.signedGen != null || this.unsignedGen != null) {
            if (this.signedGen == null) {
                this.signedGen = new DefaultSignedAttributeTableGenerator();
            }
            return new SignerInfoGenerator(sigId, contentSigner, digester, this.sigEncAlgFinder, this.signedGen, this.unsignedGen);
        }
        return new SignerInfoGenerator(sigId, contentSigner, digester, this.sigEncAlgFinder, new DefaultSignedAttributeTableGenerator(), null);
    }
}

