/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.oer.its.ieee1609dot2.CertificateBase$Builder
 *  org.bouncycastle.oer.its.ieee1609dot2.CertificateId
 *  org.bouncycastle.oer.its.ieee1609dot2.CertificateType
 *  org.bouncycastle.oer.its.ieee1609dot2.IssuerIdentifier
 *  org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate$Builder
 *  org.bouncycastle.oer.its.ieee1609dot2.VerificationKeyIndicator
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.its;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.ITSCertificateBuilder;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateBase;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateId;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateType;
import org.bouncycastle.oer.its.ieee1609dot2.IssuerIdentifier;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.VerificationKeyIndicator;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;

public class ITSImplicitCertificateBuilder
extends ITSCertificateBuilder {
    private final IssuerIdentifier issuerIdentifier;

    public ITSImplicitCertificateBuilder(ITSCertificate issuer, DigestCalculatorProvider digestCalculatorProvider, ToBeSignedCertificate.Builder tbsCertificate) {
        super(issuer, tbsCertificate);
        DigestCalculator calculator;
        AlgorithmIdentifier digestAlgId = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
        ASN1ObjectIdentifier digestAlg = digestAlgId.getAlgorithm();
        try {
            calculator = digestCalculatorProvider.get(digestAlgId);
        }
        catch (OperatorCreationException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        try {
            OutputStream os = calculator.getOutputStream();
            os.write(issuer.getEncoded());
            os.close();
        }
        catch (IOException ioex) {
            throw new IllegalStateException(ioex.getMessage(), ioex);
        }
        byte[] parentDigest = calculator.getDigest();
        HashedId8 hashedID = new HashedId8(Arrays.copyOfRange((byte[])parentDigest, (int)(parentDigest.length - 8), (int)parentDigest.length));
        if (digestAlg.equals((ASN1Primitive)NISTObjectIdentifiers.id_sha256)) {
            this.issuerIdentifier = IssuerIdentifier.sha256AndDigest((HashedId8)hashedID);
        } else if (digestAlg.equals((ASN1Primitive)NISTObjectIdentifiers.id_sha384)) {
            this.issuerIdentifier = IssuerIdentifier.sha384AndDigest((HashedId8)hashedID);
        } else {
            throw new IllegalStateException("unknown digest");
        }
    }

    public ITSCertificate build(CertificateId certificateId, BigInteger x, BigInteger y) {
        return this.build(certificateId, x, y, null);
    }

    public ITSCertificate build(CertificateId certificateId, BigInteger x, BigInteger y, PublicEncryptionKey publicEncryptionKey) {
        EccP256CurvePoint reconstructionValue = EccP256CurvePoint.uncompressedP256((BigInteger)x, (BigInteger)y);
        ToBeSignedCertificate.Builder tbsBldr = new ToBeSignedCertificate.Builder(this.tbsCertificateBuilder);
        tbsBldr.setId(certificateId);
        if (publicEncryptionKey != null) {
            tbsBldr.setEncryptionKey(publicEncryptionKey);
        }
        tbsBldr.setVerifyKeyIndicator(VerificationKeyIndicator.reconstructionValue((EccP256CurvePoint)reconstructionValue));
        CertificateBase.Builder baseBldr = new CertificateBase.Builder();
        baseBldr.setVersion(this.version);
        baseBldr.setType(CertificateType.implicit);
        baseBldr.setIssuer(this.issuerIdentifier);
        baseBldr.setToBeSigned(tbsBldr.createToBeSignedCertificate());
        return new ITSCertificate(baseBldr.createCertificateBase());
    }
}

