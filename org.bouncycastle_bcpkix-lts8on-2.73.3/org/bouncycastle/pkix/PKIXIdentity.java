/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.pkcs.PrivateKeyInfo
 *  org.bouncycastle.asn1.x509.Extensions
 *  org.bouncycastle.asn1.x509.SubjectKeyIdentifier
 */
package org.bouncycastle.pkix;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.KeyTransRecipientId;
import org.bouncycastle.cms.RecipientId;

public class PKIXIdentity {
    private final PrivateKeyInfo privateKeyInfo;
    private final X509CertificateHolder[] certificateHolders;

    public PKIXIdentity(PrivateKeyInfo privateKeyInfo, X509CertificateHolder[] certificateHolders) {
        this.privateKeyInfo = privateKeyInfo;
        this.certificateHolders = new X509CertificateHolder[certificateHolders.length];
        System.arraycopy(certificateHolders, 0, this.certificateHolders, 0, certificateHolders.length);
    }

    public PKIXIdentity(PrivateKeyInfo privateKeyInfo, X509CertificateHolder certHolder) {
        this(privateKeyInfo, new X509CertificateHolder[]{certHolder});
    }

    public PrivateKeyInfo getPrivateKeyInfo() {
        return this.privateKeyInfo;
    }

    public X509CertificateHolder getCertificate() {
        return this.certificateHolders[0];
    }

    public X509CertificateHolder[] getCertificateChain() {
        X509CertificateHolder[] rv = new X509CertificateHolder[this.certificateHolders.length];
        System.arraycopy(this.certificateHolders, 0, rv, 0, rv.length);
        return rv;
    }

    public RecipientId getRecipientId() {
        return new KeyTransRecipientId(this.certificateHolders[0].getIssuer(), this.certificateHolders[0].getSerialNumber(), this.getSubjectKeyIdentifier());
    }

    private byte[] getSubjectKeyIdentifier() {
        SubjectKeyIdentifier subId = SubjectKeyIdentifier.fromExtensions((Extensions)this.certificateHolders[0].getExtensions());
        if (subId == null) {
            return null;
        }
        return subId.getKeyIdentifier();
    }
}

