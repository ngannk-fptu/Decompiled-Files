/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.misc;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class CAST5CBCParameters
extends ASN1Object {
    ASN1Integer keyLength;
    ASN1OctetString iv;

    public static CAST5CBCParameters getInstance(Object o) {
        if (o instanceof CAST5CBCParameters) {
            return (CAST5CBCParameters)o;
        }
        if (o != null) {
            return new CAST5CBCParameters(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public CAST5CBCParameters(byte[] iv, int keyLength) {
        this.iv = new DEROctetString(Arrays.clone(iv));
        this.keyLength = new ASN1Integer(keyLength);
    }

    private CAST5CBCParameters(ASN1Sequence seq) {
        this.iv = (ASN1OctetString)seq.getObjectAt(0);
        this.keyLength = (ASN1Integer)seq.getObjectAt(1);
    }

    public byte[] getIV() {
        return Arrays.clone(this.iv.getOctets());
    }

    public int getKeyLength() {
        return this.keyLength.intValueExact();
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add(this.iv);
        v.add(this.keyLength);
        return new DERSequence(v);
    }
}

