/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.oer.Element
 *  org.bouncycastle.oer.OEREncoder
 *  org.bouncycastle.oer.its.ieee1609dot2.CertificateBase
 *  org.bouncycastle.oer.its.ieee1609dot2.IssuerIdentifier
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature
 *  org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2
 *  org.bouncycastle.util.Encodable
 */
package org.bouncycastle.its;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.its.ITSPublicEncryptionKey;
import org.bouncycastle.its.ITSValidityPeriod;
import org.bouncycastle.its.operator.ECDSAEncoder;
import org.bouncycastle.its.operator.ITSContentVerifierProvider;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.OEREncoder;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateBase;
import org.bouncycastle.oer.its.ieee1609dot2.IssuerIdentifier;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.util.Encodable;

public class ITSCertificate
implements Encodable {
    private final CertificateBase certificate;

    public ITSCertificate(CertificateBase certificate) {
        this.certificate = certificate;
    }

    public IssuerIdentifier getIssuer() {
        return this.certificate.getIssuer();
    }

    public ITSValidityPeriod getValidityPeriod() {
        return new ITSValidityPeriod(this.certificate.getToBeSigned().getValidityPeriod());
    }

    public ITSPublicEncryptionKey getPublicEncryptionKey() {
        PublicEncryptionKey encryptionKey = this.certificate.getToBeSigned().getEncryptionKey();
        if (encryptionKey != null) {
            return new ITSPublicEncryptionKey(encryptionKey);
        }
        return null;
    }

    public boolean isSignatureValid(ITSContentVerifierProvider verifierProvider) throws Exception {
        ContentVerifier contentVerifier = verifierProvider.get(this.certificate.getSignature().getChoice());
        OutputStream verOut = contentVerifier.getOutputStream();
        verOut.write(OEREncoder.toByteArray((ASN1Encodable)this.certificate.getToBeSigned(), (Element)IEEE1609dot2.ToBeSignedCertificate.build()));
        verOut.close();
        Signature sig = this.certificate.getSignature();
        return contentVerifier.verify(ECDSAEncoder.toX962(sig));
    }

    public CertificateBase toASN1Structure() {
        return this.certificate;
    }

    public byte[] getEncoded() throws IOException {
        return OEREncoder.toByteArray((ASN1Encodable)this.certificate, (Element)IEEE1609dot2.CertificateBase.build());
    }
}

