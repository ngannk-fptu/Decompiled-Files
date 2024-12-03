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
import org.bouncycastle.asn1.cmp.CMPCertificate;

public class CAKeyUpdAnnContent
extends ASN1Object {
    private final CMPCertificate oldWithNew;
    private final CMPCertificate newWithOld;
    private final CMPCertificate newWithNew;

    private CAKeyUpdAnnContent(ASN1Sequence seq) {
        this.oldWithNew = CMPCertificate.getInstance(seq.getObjectAt(0));
        this.newWithOld = CMPCertificate.getInstance(seq.getObjectAt(1));
        this.newWithNew = CMPCertificate.getInstance(seq.getObjectAt(2));
    }

    public CAKeyUpdAnnContent(CMPCertificate oldWithNew, CMPCertificate newWithOld, CMPCertificate newWithNew) {
        this.oldWithNew = oldWithNew;
        this.newWithOld = newWithOld;
        this.newWithNew = newWithNew;
    }

    public static CAKeyUpdAnnContent getInstance(Object o) {
        if (o instanceof CAKeyUpdAnnContent) {
            return (CAKeyUpdAnnContent)((Object)o);
        }
        if (o != null) {
            return new CAKeyUpdAnnContent(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public CMPCertificate getOldWithNew() {
        return this.oldWithNew;
    }

    public CMPCertificate getNewWithOld() {
        return this.newWithOld;
    }

    public CMPCertificate getNewWithNew() {
        return this.newWithNew;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.oldWithNew);
        v.add((ASN1Encodable)this.newWithOld);
        v.add((ASN1Encodable)this.newWithNew);
        return new DERSequence(v);
    }
}

