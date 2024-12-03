/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1GeneralizedTime
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class PendInfo
extends ASN1Object {
    private final byte[] pendToken;
    private final ASN1GeneralizedTime pendTime;

    public PendInfo(byte[] pendToken, ASN1GeneralizedTime pendTime) {
        this.pendToken = Arrays.clone((byte[])pendToken);
        this.pendTime = pendTime;
    }

    private PendInfo(ASN1Sequence seq) {
        if (seq.size() != 2) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.pendToken = Arrays.clone((byte[])ASN1OctetString.getInstance((Object)seq.getObjectAt(0)).getOctets());
        this.pendTime = ASN1GeneralizedTime.getInstance((Object)seq.getObjectAt(1));
    }

    public static PendInfo getInstance(Object o) {
        if (o instanceof PendInfo) {
            return (PendInfo)((Object)o);
        }
        if (o != null) {
            return new PendInfo(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)new DEROctetString(this.pendToken));
        v.add((ASN1Encodable)this.pendTime);
        return new DERSequence(v);
    }

    public byte[] getPendToken() {
        return Arrays.clone((byte[])this.pendToken);
    }

    public ASN1GeneralizedTime getPendTime() {
        return this.pendTime;
    }
}

