/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class CCMParameters
extends ASN1Object {
    private byte[] nonce;
    private int icvLen;

    public static CCMParameters getInstance(Object obj) {
        if (obj instanceof CCMParameters) {
            return (CCMParameters)((Object)obj);
        }
        if (obj != null) {
            return new CCMParameters(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    private CCMParameters(ASN1Sequence seq) {
        this.nonce = ASN1OctetString.getInstance((Object)seq.getObjectAt(0)).getOctets();
        this.icvLen = seq.size() == 2 ? ASN1Integer.getInstance((Object)seq.getObjectAt(1)).intValueExact() : 12;
    }

    public CCMParameters(byte[] nonce, int icvLen) {
        this.nonce = Arrays.clone((byte[])nonce);
        this.icvLen = icvLen;
    }

    public byte[] getNonce() {
        return Arrays.clone((byte[])this.nonce);
    }

    public int getIcvLen() {
        return this.icvLen;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)new DEROctetString(this.nonce));
        if (this.icvLen != 12) {
            v.add((ASN1Encodable)new ASN1Integer((long)this.icvLen));
        }
        return new DERSequence(v);
    }
}

