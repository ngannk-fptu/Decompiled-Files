/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.GeneralName
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 */
package org.bouncycastle.cert.crmf.jcajce;

import java.math.BigInteger;
import java.security.PublicKey;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.crmf.CertificateRequestMessageBuilder;

public class JcaCertificateRequestMessageBuilder
extends CertificateRequestMessageBuilder {
    public JcaCertificateRequestMessageBuilder(BigInteger certReqId) {
        super(certReqId);
    }

    public JcaCertificateRequestMessageBuilder setIssuer(X500Principal issuer) {
        if (issuer != null) {
            this.setIssuer(X500Name.getInstance((Object)issuer.getEncoded()));
        }
        return this;
    }

    public JcaCertificateRequestMessageBuilder setSubject(X500Principal subject) {
        if (subject != null) {
            this.setSubject(X500Name.getInstance((Object)subject.getEncoded()));
        }
        return this;
    }

    public JcaCertificateRequestMessageBuilder setAuthInfoSender(X500Principal sender) {
        if (sender != null) {
            this.setAuthInfoSender(new GeneralName(X500Name.getInstance((Object)sender.getEncoded())));
        }
        return this;
    }

    public JcaCertificateRequestMessageBuilder setPublicKey(PublicKey publicKey) {
        this.setPublicKey(SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()));
        return this;
    }
}

