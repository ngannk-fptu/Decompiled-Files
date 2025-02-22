/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.security.cert.CertificateEncodingException;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.jcajce.provider.asymmetric.x509.X509CertificateImpl;
import org.bouncycastle.jcajce.util.JcaJceHelper;

class X509CertificateInternal
extends X509CertificateImpl {
    private final byte[] encoding;
    private final CertificateEncodingException exception;

    X509CertificateInternal(JcaJceHelper bcHelper, Certificate c, BasicConstraints basicConstraints, boolean[] keyUsage, String sigAlgName, byte[] sigAlgParams, byte[] encoding, CertificateEncodingException exception) {
        super(bcHelper, c, basicConstraints, keyUsage, sigAlgName, sigAlgParams);
        this.encoding = encoding;
        this.exception = exception;
    }

    @Override
    public byte[] getEncoded() throws CertificateEncodingException {
        if (null != this.exception) {
            throw this.exception;
        }
        if (null == this.encoding) {
            throw new CertificateEncodingException();
        }
        return this.encoding;
    }
}

