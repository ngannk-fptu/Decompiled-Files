/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.cmp.CMPCertificate
 *  org.bouncycastle.asn1.cmp.CertStatus
 *  org.bouncycastle.asn1.cmp.PKIStatusInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.cert.cmp;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertStatus;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.cmp.CMPException;
import org.bouncycastle.cert.cmp.CMPUtil;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;

public class CertificateStatus {
    private DigestAlgorithmIdentifierFinder digestAlgFinder;
    private CertStatus certStatus;

    CertificateStatus(DigestAlgorithmIdentifierFinder digestAlgFinder, CertStatus certStatus) {
        this.digestAlgFinder = digestAlgFinder;
        this.certStatus = certStatus;
    }

    public PKIStatusInfo getStatusInfo() {
        return this.certStatus.getStatusInfo();
    }

    public BigInteger getCertRequestID() {
        return this.certStatus.getCertReqId().getValue();
    }

    public boolean isVerified(X509CertificateHolder certHolder, DigestCalculatorProvider digesterProvider) throws CMPException {
        return this.isVerified(new CMPCertificate(certHolder.toASN1Structure()), certHolder.getSignatureAlgorithm(), digesterProvider);
    }

    public boolean isVerified(CMPCertificate cmpCert, AlgorithmIdentifier signatureAlgorithm, DigestCalculatorProvider digesterProvider) throws CMPException {
        DigestCalculator digester;
        AlgorithmIdentifier digAlg = this.digestAlgFinder.find(signatureAlgorithm);
        if (digAlg == null) {
            throw new CMPException("cannot find algorithm for digest from signature");
        }
        try {
            digester = digesterProvider.get(digAlg);
        }
        catch (OperatorCreationException e) {
            throw new CMPException("unable to create digester: " + e.getMessage(), e);
        }
        CMPUtil.derEncodeToStream((ASN1Object)cmpCert, digester.getOutputStream());
        return Arrays.areEqual((byte[])this.certStatus.getCertHash().getOctets(), (byte[])digester.getDigest());
    }
}

