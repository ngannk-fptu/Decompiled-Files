/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.openssl;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.CertificateTrustBlock;
import org.bouncycastle.util.Arrays;

public class X509TrustedCertificateBlock {
    private final X509CertificateHolder certificateHolder;
    private final CertificateTrustBlock trustBlock;

    public X509TrustedCertificateBlock(X509CertificateHolder x509CertificateHolder, CertificateTrustBlock certificateTrustBlock) {
        this.certificateHolder = x509CertificateHolder;
        this.trustBlock = certificateTrustBlock;
    }

    public X509TrustedCertificateBlock(byte[] byArray) throws IOException {
        ASN1InputStream aSN1InputStream = new ASN1InputStream(byArray);
        this.certificateHolder = new X509CertificateHolder(aSN1InputStream.readObject().getEncoded());
        ASN1Primitive aSN1Primitive = aSN1InputStream.readObject();
        this.trustBlock = aSN1Primitive != null ? new CertificateTrustBlock(aSN1Primitive.getEncoded()) : null;
    }

    public byte[] getEncoded() throws IOException {
        return Arrays.concatenate(this.certificateHolder.getEncoded(), this.trustBlock.toASN1Sequence().getEncoded());
    }

    public X509CertificateHolder getCertificateHolder() {
        return this.certificateHolder;
    }

    public CertificateTrustBlock getTrustBlock() {
        return this.trustBlock;
    }
}

