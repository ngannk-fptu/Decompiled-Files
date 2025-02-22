/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.internal.asn1.cms;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class GCMParameters
extends ASN1Object {
    private byte[] nonce;
    private int icvLen;

    public static GCMParameters getInstance(Object obj) {
        if (obj instanceof GCMParameters) {
            return (GCMParameters)obj;
        }
        if (obj != null) {
            return new GCMParameters(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private GCMParameters(ASN1Sequence seq) {
        this.nonce = ASN1OctetString.getInstance(seq.getObjectAt(0)).getOctets();
        this.icvLen = seq.size() == 2 ? ASN1Integer.getInstance(seq.getObjectAt(1)).intValueExact() : 12;
    }

    public GCMParameters(byte[] nonce, int icvLen) {
        this.nonce = Arrays.clone(nonce);
        this.icvLen = icvLen;
    }

    public byte[] getNonce() {
        return Arrays.clone(this.nonce);
    }

    public int getIcvLen() {
        return this.icvLen;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add(new DEROctetString(this.nonce));
        if (this.icvLen != 12) {
            v.add(new ASN1Integer(this.icvLen));
        }
        return new DERSequence(v);
    }
}

