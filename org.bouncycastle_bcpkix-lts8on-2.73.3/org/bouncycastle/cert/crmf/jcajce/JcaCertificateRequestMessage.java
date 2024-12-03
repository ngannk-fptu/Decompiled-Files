/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.crmf.CertReqMsg
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 */
package org.bouncycastle.cert.crmf.jcajce;

import java.io.IOException;
import java.security.Provider;
import java.security.PublicKey;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.CertificateRequestMessage;
import org.bouncycastle.cert.crmf.jcajce.CRMFHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;

public class JcaCertificateRequestMessage
extends CertificateRequestMessage {
    private CRMFHelper helper = new CRMFHelper((JcaJceHelper)new DefaultJcaJceHelper());

    public JcaCertificateRequestMessage(byte[] certReqMsg) {
        this(CertReqMsg.getInstance((Object)certReqMsg));
    }

    public JcaCertificateRequestMessage(CertificateRequestMessage certReqMsg) {
        this(certReqMsg.toASN1Structure());
    }

    public JcaCertificateRequestMessage(CertReqMsg certReqMsg) {
        super(certReqMsg);
    }

    public JcaCertificateRequestMessage setProvider(String providerName) {
        this.helper = new CRMFHelper((JcaJceHelper)new NamedJcaJceHelper(providerName));
        return this;
    }

    public JcaCertificateRequestMessage setProvider(Provider provider) {
        this.helper = new CRMFHelper((JcaJceHelper)new ProviderJcaJceHelper(provider));
        return this;
    }

    public X500Principal getSubjectX500Principal() {
        X500Name subject = this.getCertTemplate().getSubject();
        if (subject != null) {
            try {
                return new X500Principal(subject.getEncoded("DER"));
            }
            catch (IOException e) {
                throw new IllegalStateException("unable to construct DER encoding of name: " + e.getMessage());
            }
        }
        return null;
    }

    public PublicKey getPublicKey() throws CRMFException {
        SubjectPublicKeyInfo subjectPublicKeyInfo = this.getCertTemplate().getPublicKey();
        if (subjectPublicKeyInfo != null) {
            return this.helper.toPublicKey(subjectPublicKeyInfo);
        }
        return null;
    }
}

