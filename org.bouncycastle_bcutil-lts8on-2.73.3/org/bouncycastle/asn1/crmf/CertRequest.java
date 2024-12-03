/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.crmf.CertTemplate;
import org.bouncycastle.asn1.crmf.Controls;

public class CertRequest
extends ASN1Object {
    private ASN1Integer certReqId;
    private CertTemplate certTemplate;
    private Controls controls;

    private CertRequest(ASN1Sequence seq) {
        this.certReqId = new ASN1Integer(ASN1Integer.getInstance((Object)seq.getObjectAt(0)).getValue());
        this.certTemplate = CertTemplate.getInstance(seq.getObjectAt(1));
        if (seq.size() > 2) {
            this.controls = Controls.getInstance(seq.getObjectAt(2));
        }
    }

    public static CertRequest getInstance(Object o) {
        if (o instanceof CertRequest) {
            return (CertRequest)((Object)o);
        }
        if (o != null) {
            return new CertRequest(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public CertRequest(int certReqId, CertTemplate certTemplate, Controls controls) {
        this(new ASN1Integer((long)certReqId), certTemplate, controls);
    }

    public CertRequest(ASN1Integer certReqId, CertTemplate certTemplate, Controls controls) {
        this.certReqId = certReqId;
        this.certTemplate = certTemplate;
        this.controls = controls;
    }

    public ASN1Integer getCertReqId() {
        return this.certReqId;
    }

    public CertTemplate getCertTemplate() {
        return this.certTemplate;
    }

    public Controls getControls() {
        return this.controls;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.certReqId);
        v.add((ASN1Encodable)this.certTemplate);
        if (this.controls != null) {
            v.add((ASN1Encodable)this.controls);
        }
        return new DERSequence(v);
    }
}

