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
 *  org.bouncycastle.asn1.x509.Time
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.CRLSource;
import org.bouncycastle.asn1.x509.Time;

public class CRLStatus
extends ASN1Object {
    private final CRLSource source;
    private final Time thisUpdate;

    private CRLStatus(ASN1Sequence sequence) {
        if (sequence.size() == 1 || sequence.size() == 2) {
            this.source = CRLSource.getInstance(sequence.getObjectAt(0));
            this.thisUpdate = sequence.size() == 2 ? Time.getInstance((Object)sequence.getObjectAt(1)) : null;
        } else {
            throw new IllegalArgumentException("expected sequence size of 1 or 2, got " + sequence.size());
        }
    }

    public CRLStatus(CRLSource source, Time thisUpdate) {
        this.source = source;
        this.thisUpdate = thisUpdate;
    }

    public static CRLStatus getInstance(Object o) {
        if (o instanceof CRLStatus) {
            return (CRLStatus)((Object)o);
        }
        if (o != null) {
            return new CRLStatus(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public CRLSource getSource() {
        return this.source;
    }

    public Time getThisUpdate() {
        return this.thisUpdate;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.source);
        if (this.thisUpdate != null) {
            v.add((ASN1Encodable)this.thisUpdate);
        }
        return new DERSequence(v);
    }
}

