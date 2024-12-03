/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.crmf.CertTemplate;

public class CertReqTemplateContent
extends ASN1Object {
    private final CertTemplate certTemplate;
    private final ASN1Sequence keySpec;

    private CertReqTemplateContent(ASN1Sequence seq) {
        if (seq.size() != 1 && seq.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 1 or 2");
        }
        this.certTemplate = CertTemplate.getInstance(seq.getObjectAt(0));
        this.keySpec = seq.size() > 1 ? ASN1Sequence.getInstance((Object)seq.getObjectAt(1)) : null;
    }

    public CertReqTemplateContent(CertTemplate certTemplate, ASN1Sequence keySpec) {
        this.certTemplate = certTemplate;
        this.keySpec = keySpec;
    }

    public static CertReqTemplateContent getInstance(Object o) {
        if (o instanceof CertReqTemplateContent) {
            return (CertReqTemplateContent)((Object)o);
        }
        if (o != null) {
            return new CertReqTemplateContent(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public CertTemplate getCertTemplate() {
        return this.certTemplate;
    }

    public ASN1Sequence getKeySpec() {
        return this.keySpec;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.certTemplate);
        if (this.keySpec != null) {
            v.add((ASN1Encodable)this.keySpec);
        }
        return new DERSequence(v);
    }
}

