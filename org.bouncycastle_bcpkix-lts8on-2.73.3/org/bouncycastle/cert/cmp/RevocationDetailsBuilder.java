/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.cmp.RevDetails
 *  org.bouncycastle.asn1.crmf.CertTemplateBuilder
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 */
package org.bouncycastle.cert.cmp;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.cmp.RevDetails;
import org.bouncycastle.asn1.crmf.CertTemplateBuilder;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.cmp.RevocationDetails;

public class RevocationDetailsBuilder {
    private CertTemplateBuilder templateBuilder = new CertTemplateBuilder();

    public RevocationDetailsBuilder setPublicKey(SubjectPublicKeyInfo publicKey) {
        if (publicKey != null) {
            this.templateBuilder.setPublicKey(publicKey);
        }
        return this;
    }

    public RevocationDetailsBuilder setIssuer(X500Name issuer) {
        if (issuer != null) {
            this.templateBuilder.setIssuer(issuer);
        }
        return this;
    }

    public RevocationDetailsBuilder setSerialNumber(BigInteger serialNumber) {
        if (serialNumber != null) {
            this.templateBuilder.setSerialNumber(new ASN1Integer(serialNumber));
        }
        return this;
    }

    public RevocationDetailsBuilder setSubject(X500Name subject) {
        if (subject != null) {
            this.templateBuilder.setSubject(subject);
        }
        return this;
    }

    public RevocationDetails build() {
        return new RevocationDetails(new RevDetails(this.templateBuilder.build()));
    }
}

