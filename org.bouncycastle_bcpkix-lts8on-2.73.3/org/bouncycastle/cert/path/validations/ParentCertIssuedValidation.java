/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Null
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.util.Memoable
 */
package org.bouncycastle.cert.path.validations;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.CertException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509ContentVerifierProviderBuilder;
import org.bouncycastle.cert.path.CertPathValidation;
import org.bouncycastle.cert.path.CertPathValidationContext;
import org.bouncycastle.cert.path.CertPathValidationException;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Memoable;

public class ParentCertIssuedValidation
implements CertPathValidation {
    private X509ContentVerifierProviderBuilder contentVerifierProvider;
    private X500Name workingIssuerName;
    private SubjectPublicKeyInfo workingPublicKey;
    private AlgorithmIdentifier workingAlgId;

    public ParentCertIssuedValidation(X509ContentVerifierProviderBuilder contentVerifierProvider) {
        this.contentVerifierProvider = contentVerifierProvider;
    }

    @Override
    public void validate(CertPathValidationContext context, X509CertificateHolder certificate) throws CertPathValidationException {
        if (this.workingIssuerName != null && !this.workingIssuerName.equals((Object)certificate.getIssuer())) {
            throw new CertPathValidationException("Certificate issue does not match parent");
        }
        if (this.workingPublicKey != null) {
            try {
                SubjectPublicKeyInfo validatingKeyInfo = this.workingPublicKey.getAlgorithm().equals((Object)this.workingAlgId) ? this.workingPublicKey : new SubjectPublicKeyInfo(this.workingAlgId, (ASN1Encodable)this.workingPublicKey.parsePublicKey());
                if (!certificate.isSignatureValid(this.contentVerifierProvider.build(validatingKeyInfo))) {
                    throw new CertPathValidationException("Certificate signature not for public key in parent");
                }
            }
            catch (OperatorCreationException e) {
                throw new CertPathValidationException("Unable to create verifier: " + e.getMessage(), e);
            }
            catch (CertException e) {
                throw new CertPathValidationException("Unable to validate signature: " + e.getMessage(), e);
            }
            catch (IOException e) {
                throw new CertPathValidationException("Unable to build public key: " + e.getMessage(), e);
            }
        }
        this.workingIssuerName = certificate.getSubject();
        this.workingPublicKey = certificate.getSubjectPublicKeyInfo();
        if (this.workingAlgId != null) {
            if (this.workingPublicKey.getAlgorithm().getAlgorithm().equals((ASN1Primitive)this.workingAlgId.getAlgorithm())) {
                if (!this.isNull(this.workingPublicKey.getAlgorithm().getParameters())) {
                    this.workingAlgId = this.workingPublicKey.getAlgorithm();
                }
            } else {
                this.workingAlgId = this.workingPublicKey.getAlgorithm();
            }
        } else {
            this.workingAlgId = this.workingPublicKey.getAlgorithm();
        }
    }

    private boolean isNull(ASN1Encodable obj) {
        return obj == null || obj instanceof ASN1Null;
    }

    public Memoable copy() {
        ParentCertIssuedValidation v = new ParentCertIssuedValidation(this.contentVerifierProvider);
        v.workingAlgId = this.workingAlgId;
        v.workingIssuerName = this.workingIssuerName;
        v.workingPublicKey = this.workingPublicKey;
        return v;
    }

    public void reset(Memoable other) {
        ParentCertIssuedValidation v = (ParentCertIssuedValidation)other;
        this.contentVerifierProvider = v.contentVerifierProvider;
        this.workingAlgId = v.workingAlgId;
        this.workingIssuerName = v.workingIssuerName;
        this.workingPublicKey = v.workingPublicKey;
    }
}

