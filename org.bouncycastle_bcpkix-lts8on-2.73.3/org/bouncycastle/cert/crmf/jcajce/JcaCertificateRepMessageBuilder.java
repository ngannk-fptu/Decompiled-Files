/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.crmf.jcajce;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.crmf.CertificateRepMessageBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

public class JcaCertificateRepMessageBuilder
extends CertificateRepMessageBuilder {
    public JcaCertificateRepMessageBuilder(X509Certificate ... caCertificates) throws CertificateEncodingException {
        super(JcaCertificateRepMessageBuilder.convert(caCertificates));
    }

    private static X509CertificateHolder[] convert(X509Certificate ... certificates) throws CertificateEncodingException {
        X509CertificateHolder[] certs = new X509CertificateHolder[certificates.length];
        for (int i = 0; i != certs.length; ++i) {
            certs[i] = new JcaX509CertificateHolder(certificates[i]);
        }
        return certs;
    }
}

