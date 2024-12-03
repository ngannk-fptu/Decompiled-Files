/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.crypto.params.AsymmetricKeyParameter
 *  org.bouncycastle.crypto.params.ECPublicKeyParameters
 *  org.bouncycastle.oer.its.ieee1609dot2.CertificateId
 *  org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate$Builder
 */
package org.bouncycastle.its.bc;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.ITSExplicitCertificateBuilder;
import org.bouncycastle.its.bc.BcITSPublicEncryptionKey;
import org.bouncycastle.its.bc.BcITSPublicVerificationKey;
import org.bouncycastle.its.operator.ITSContentSigner;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateId;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;

public class BcITSExplicitCertificateBuilder
extends ITSExplicitCertificateBuilder {
    public BcITSExplicitCertificateBuilder(ITSContentSigner signer, ToBeSignedCertificate.Builder tbsCertificate) {
        super(signer, tbsCertificate);
    }

    public ITSCertificate build(CertificateId certificateId, ECPublicKeyParameters verificationKey) {
        return this.build(certificateId, verificationKey, null);
    }

    public ITSCertificate build(CertificateId certificateId, ECPublicKeyParameters verificationKey, ECPublicKeyParameters encryptionKey) {
        BcITSPublicEncryptionKey publicEncryptionKey = null;
        if (encryptionKey != null) {
            publicEncryptionKey = new BcITSPublicEncryptionKey((AsymmetricKeyParameter)encryptionKey);
        }
        return super.build(certificateId, new BcITSPublicVerificationKey((AsymmetricKeyParameter)verificationKey), publicEncryptionKey);
    }
}

