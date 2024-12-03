/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1InputStream
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.util.Arrays
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

    public X509TrustedCertificateBlock(X509CertificateHolder certificateHolder, CertificateTrustBlock trustBlock) {
        this.certificateHolder = certificateHolder;
        this.trustBlock = trustBlock;
    }

    public X509TrustedCertificateBlock(byte[] encoding) throws IOException {
        ASN1InputStream aIn = new ASN1InputStream(encoding);
        this.certificateHolder = new X509CertificateHolder(aIn.readObject().getEncoded());
        ASN1Primitive tBlock = aIn.readObject();
        this.trustBlock = tBlock != null ? new CertificateTrustBlock(tBlock.getEncoded()) : null;
    }

    public byte[] getEncoded() throws IOException {
        return Arrays.concatenate((byte[])this.certificateHolder.getEncoded(), (byte[])this.trustBlock.toASN1Sequence().getEncoded());
    }

    public X509CertificateHolder getCertificateHolder() {
        return this.certificateHolder;
    }

    public CertificateTrustBlock getTrustBlock() {
        return this.trustBlock;
    }
}

