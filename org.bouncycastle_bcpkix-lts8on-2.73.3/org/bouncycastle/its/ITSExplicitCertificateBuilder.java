/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.sec.SECObjectIdentifiers
 *  org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers
 *  org.bouncycastle.oer.Element
 *  org.bouncycastle.oer.OEREncoder
 *  org.bouncycastle.oer.its.ieee1609dot2.CertificateBase$Builder
 *  org.bouncycastle.oer.its.ieee1609dot2.CertificateId
 *  org.bouncycastle.oer.its.ieee1609dot2.CertificateType
 *  org.bouncycastle.oer.its.ieee1609dot2.IssuerIdentifier
 *  org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate
 *  org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate$Builder
 *  org.bouncycastle.oer.its.ieee1609dot2.VerificationKeyIndicator
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashAlgorithm
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature
 *  org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.its;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.ITSCertificateBuilder;
import org.bouncycastle.its.ITSPublicEncryptionKey;
import org.bouncycastle.its.ITSPublicVerificationKey;
import org.bouncycastle.its.operator.ECDSAEncoder;
import org.bouncycastle.its.operator.ITSContentSigner;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.OEREncoder;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateBase;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateId;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateType;
import org.bouncycastle.oer.its.ieee1609dot2.IssuerIdentifier;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.VerificationKeyIndicator;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashAlgorithm;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;
import org.bouncycastle.util.Arrays;

public class ITSExplicitCertificateBuilder
extends ITSCertificateBuilder {
    private final ITSContentSigner signer;

    public ITSExplicitCertificateBuilder(ITSContentSigner signer, ToBeSignedCertificate.Builder tbsCertificate) {
        super(tbsCertificate);
        this.signer = signer;
    }

    public ITSCertificate build(CertificateId certificateId, ITSPublicVerificationKey verificationKey) {
        return this.build(certificateId, verificationKey, null);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public ITSCertificate build(CertificateId certificateId, ITSPublicVerificationKey verificationKey, ITSPublicEncryptionKey publicEncryptionKey) {
        IssuerIdentifier issuerIdentifier;
        VerificationKeyIndicator verificationKeyIndicator;
        ToBeSignedCertificate.Builder tbsBldr = new ToBeSignedCertificate.Builder(this.tbsCertificateBuilder);
        tbsBldr.setId(certificateId);
        if (publicEncryptionKey != null) {
            tbsBldr.setEncryptionKey(publicEncryptionKey.toASN1Structure());
        }
        tbsBldr.setVerifyKeyIndicator(VerificationKeyIndicator.verificationKey((PublicVerificationKey)verificationKey.toASN1Structure()));
        ToBeSignedCertificate tbsCertificate = tbsBldr.createToBeSignedCertificate();
        ToBeSignedCertificate signerCert = null;
        if (this.signer.isForSelfSigning()) {
            verificationKeyIndicator = tbsCertificate.getVerifyKeyIndicator();
        } else {
            signerCert = this.signer.getAssociatedCertificate().toASN1Structure().getToBeSigned();
            verificationKeyIndicator = signerCert.getVerifyKeyIndicator();
        }
        OutputStream sOut = this.signer.getOutputStream();
        try {
            sOut.write(OEREncoder.toByteArray((ASN1Encodable)tbsCertificate, (Element)IEEE1609dot2.ToBeSignedCertificate.build()));
            sOut.close();
        }
        catch (IOException e) {
            throw new IllegalArgumentException("cannot produce certificate signature");
        }
        Signature sig = null;
        switch (verificationKeyIndicator.getChoice()) {
            case 0: {
                sig = ECDSAEncoder.toITS(SECObjectIdentifiers.secp256r1, this.signer.getSignature());
                break;
            }
            case 1: {
                sig = ECDSAEncoder.toITS(TeleTrusTObjectIdentifiers.brainpoolP256r1, this.signer.getSignature());
                break;
            }
            case 2: {
                sig = ECDSAEncoder.toITS(TeleTrusTObjectIdentifiers.brainpoolP384r1, this.signer.getSignature());
                break;
            }
            default: {
                throw new IllegalStateException("unknown key type");
            }
        }
        CertificateBase.Builder baseBldr = new CertificateBase.Builder();
        ASN1ObjectIdentifier digestAlg = this.signer.getDigestAlgorithm().getAlgorithm();
        if (this.signer.isForSelfSigning()) {
            if (digestAlg.equals((ASN1Primitive)NISTObjectIdentifiers.id_sha256)) {
                issuerIdentifier = IssuerIdentifier.self((HashAlgorithm)HashAlgorithm.sha256);
            } else {
                if (!digestAlg.equals((ASN1Primitive)NISTObjectIdentifiers.id_sha384)) throw new IllegalStateException("unknown digest");
                issuerIdentifier = IssuerIdentifier.self((HashAlgorithm)HashAlgorithm.sha384);
            }
        } else {
            byte[] parentDigest = this.signer.getAssociatedCertificateDigest();
            HashedId8 hashedID = new HashedId8(Arrays.copyOfRange((byte[])parentDigest, (int)(parentDigest.length - 8), (int)parentDigest.length));
            if (digestAlg.equals((ASN1Primitive)NISTObjectIdentifiers.id_sha256)) {
                issuerIdentifier = IssuerIdentifier.sha256AndDigest((HashedId8)hashedID);
            } else {
                if (!digestAlg.equals((ASN1Primitive)NISTObjectIdentifiers.id_sha384)) throw new IllegalStateException("unknown digest");
                issuerIdentifier = IssuerIdentifier.sha384AndDigest((HashedId8)hashedID);
            }
        }
        baseBldr.setVersion(this.version);
        baseBldr.setType(CertificateType.explicit);
        baseBldr.setIssuer(issuerIdentifier);
        baseBldr.setToBeSigned(tbsCertificate);
        baseBldr.setSignature(sig);
        return new ITSCertificate(baseBldr.createCertificateBase());
    }
}

