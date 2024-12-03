/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.crmf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.crmf.AttributeTypeAndValue;
import org.bouncycastle.asn1.crmf.CertRequest;
import org.bouncycastle.asn1.crmf.ProofOfPossession;

public class CertReqMsg
extends ASN1Object {
    private CertRequest certReq;
    private ProofOfPossession pop;
    private ASN1Sequence regInfo;

    private CertReqMsg(ASN1Sequence seq) {
        Enumeration en = seq.getObjects();
        this.certReq = CertRequest.getInstance(en.nextElement());
        while (en.hasMoreElements()) {
            Object o = en.nextElement();
            if (o instanceof ASN1TaggedObject || o instanceof ProofOfPossession) {
                this.pop = ProofOfPossession.getInstance(o);
                continue;
            }
            this.regInfo = ASN1Sequence.getInstance(o);
        }
    }

    public static CertReqMsg getInstance(Object o) {
        if (o instanceof CertReqMsg) {
            return (CertReqMsg)((Object)o);
        }
        if (o != null) {
            return new CertReqMsg(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public static CertReqMsg getInstance(ASN1TaggedObject obj, boolean explicit) {
        return CertReqMsg.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public CertReqMsg(CertRequest certReq, ProofOfPossession pop, AttributeTypeAndValue[] regInfo) {
        if (certReq == null) {
            throw new IllegalArgumentException("'certReq' cannot be null");
        }
        this.certReq = certReq;
        this.pop = pop;
        if (regInfo != null) {
            this.regInfo = new DERSequence((ASN1Encodable[])regInfo);
        }
    }

    public CertRequest getCertReq() {
        return this.certReq;
    }

    public ProofOfPossession getPop() {
        return this.pop;
    }

    public ProofOfPossession getPopo() {
        return this.pop;
    }

    public AttributeTypeAndValue[] getRegInfo() {
        if (this.regInfo == null) {
            return null;
        }
        AttributeTypeAndValue[] results = new AttributeTypeAndValue[this.regInfo.size()];
        for (int i = 0; i != results.length; ++i) {
            results[i] = AttributeTypeAndValue.getInstance(this.regInfo.getObjectAt(i));
        }
        return results;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.certReq);
        this.addOptional(v, (ASN1Encodable)this.pop);
        this.addOptional(v, (ASN1Encodable)this.regInfo);
        return new DERSequence(v);
    }

    private void addOptional(ASN1EncodableVector v, ASN1Encodable obj) {
        if (obj != null) {
            v.add(obj);
        }
    }
}

