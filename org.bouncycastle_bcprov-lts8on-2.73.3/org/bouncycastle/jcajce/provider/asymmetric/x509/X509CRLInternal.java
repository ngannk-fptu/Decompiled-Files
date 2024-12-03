/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.security.cert.CRLException;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.jcajce.provider.asymmetric.x509.X509CRLImpl;
import org.bouncycastle.jcajce.util.JcaJceHelper;

class X509CRLInternal
extends X509CRLImpl {
    private final byte[] encoding;
    private final CRLException exception;

    X509CRLInternal(JcaJceHelper bcHelper, CertificateList c, String sigAlgName, byte[] sigAlgParams, boolean isIndirect, byte[] encoding, CRLException exception) {
        super(bcHelper, c, sigAlgName, sigAlgParams, isIndirect);
        this.encoding = encoding;
        this.exception = exception;
    }

    @Override
    public byte[] getEncoded() throws CRLException {
        if (null != this.exception) {
            throw this.exception;
        }
        if (null == this.encoding) {
            throw new CRLException();
        }
        return this.encoding;
    }
}

