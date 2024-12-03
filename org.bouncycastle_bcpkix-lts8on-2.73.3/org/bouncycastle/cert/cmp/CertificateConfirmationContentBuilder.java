/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.cmp.CMPCertificate
 *  org.bouncycastle.asn1.cmp.CertConfirmContent
 *  org.bouncycastle.asn1.cmp.CertStatus
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.cert.cmp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertConfirmContent;
import org.bouncycastle.asn1.cmp.CertStatus;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.cmp.CMPException;
import org.bouncycastle.cert.cmp.CMPUtil;
import org.bouncycastle.cert.cmp.CertificateConfirmationContent;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

public class CertificateConfirmationContentBuilder {
    private DigestAlgorithmIdentifierFinder digestAlgFinder;
    private List<CMPCertificate> acceptedCerts = new ArrayList<CMPCertificate>();
    private List<AlgorithmIdentifier> acceptedSignatureAlgorithms = new ArrayList<AlgorithmIdentifier>();
    private List<ASN1Integer> acceptedReqIds = new ArrayList<ASN1Integer>();

    public CertificateConfirmationContentBuilder() {
        this(new DefaultDigestAlgorithmIdentifierFinder());
    }

    public CertificateConfirmationContentBuilder(DigestAlgorithmIdentifierFinder digestAlgFinder) {
        this.digestAlgFinder = digestAlgFinder;
    }

    public CertificateConfirmationContentBuilder addAcceptedCertificate(X509CertificateHolder certHolder, BigInteger certReqID) {
        return this.addAcceptedCertificate(certHolder, new ASN1Integer(certReqID));
    }

    public CertificateConfirmationContentBuilder addAcceptedCertificate(X509CertificateHolder certHolder, ASN1Integer certReqID) {
        return this.addAcceptedCertificate(new CMPCertificate(certHolder.toASN1Structure()), certHolder.getSignatureAlgorithm(), certReqID);
    }

    public CertificateConfirmationContentBuilder addAcceptedCertificate(CMPCertificate cmpCertificate, AlgorithmIdentifier sigAlg, ASN1Integer certReqID) {
        this.acceptedCerts.add(cmpCertificate);
        this.acceptedSignatureAlgorithms.add(sigAlg);
        this.acceptedReqIds.add(certReqID);
        return this;
    }

    public CertificateConfirmationContent build(DigestCalculatorProvider digesterProvider) throws CMPException {
        ASN1EncodableVector v = new ASN1EncodableVector();
        for (int i = 0; i != this.acceptedCerts.size(); ++i) {
            DigestCalculator digester;
            CMPCertificate certHolder = this.acceptedCerts.get(i);
            ASN1Integer reqID = this.acceptedReqIds.get(i);
            AlgorithmIdentifier digAlg = this.digestAlgFinder.find(this.acceptedSignatureAlgorithms.get(i));
            if (digAlg == null) {
                throw new CMPException("cannot find algorithm for digest from signature");
            }
            try {
                digester = digesterProvider.get(digAlg);
            }
            catch (OperatorCreationException e) {
                throw new CMPException("unable to create digest: " + e.getMessage(), e);
            }
            CMPUtil.derEncodeToStream((ASN1Object)certHolder, digester.getOutputStream());
            v.add((ASN1Encodable)new CertStatus(digester.getDigest(), reqID));
        }
        return new CertificateConfirmationContent(CertConfirmContent.getInstance((Object)new DERSequence(v)), this.digestAlgFinder);
    }
}

