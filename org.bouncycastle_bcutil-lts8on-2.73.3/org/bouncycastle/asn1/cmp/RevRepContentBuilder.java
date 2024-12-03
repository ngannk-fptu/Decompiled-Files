/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.CertificateList
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.cmp.RevRepContent;
import org.bouncycastle.asn1.crmf.CertId;
import org.bouncycastle.asn1.x509.CertificateList;

public class RevRepContentBuilder {
    private final ASN1EncodableVector status = new ASN1EncodableVector();
    private final ASN1EncodableVector revCerts = new ASN1EncodableVector();
    private final ASN1EncodableVector crls = new ASN1EncodableVector();

    public RevRepContentBuilder add(PKIStatusInfo status) {
        this.status.add((ASN1Encodable)status);
        return this;
    }

    public RevRepContentBuilder add(PKIStatusInfo status, CertId certId) {
        if (this.status.size() != this.revCerts.size()) {
            throw new IllegalStateException("status and revCerts sequence must be in common order");
        }
        this.status.add((ASN1Encodable)status);
        this.revCerts.add((ASN1Encodable)certId);
        return this;
    }

    public RevRepContentBuilder addCrl(CertificateList crl) {
        this.crls.add((ASN1Encodable)crl);
        return this;
    }

    public RevRepContent build() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)new DERSequence(this.status));
        if (this.revCerts.size() != 0) {
            v.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)new DERSequence(this.revCerts)));
        }
        if (this.crls.size() != 0) {
            v.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)new DERSequence(this.crls)));
        }
        return RevRepContent.getInstance(new DERSequence(v));
    }
}

