/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.misc;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class IDEACBCPar
extends ASN1Object {
    ASN1OctetString iv;

    public static IDEACBCPar getInstance(Object o) {
        if (o instanceof IDEACBCPar) {
            return (IDEACBCPar)o;
        }
        if (o != null) {
            return new IDEACBCPar(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public IDEACBCPar(byte[] iv) {
        this.iv = new DEROctetString(Arrays.clone(iv));
    }

    private IDEACBCPar(ASN1Sequence seq) {
        this.iv = seq.size() == 1 ? (ASN1OctetString)seq.getObjectAt(0) : null;
    }

    public byte[] getIV() {
        if (this.iv != null) {
            return Arrays.clone(this.iv.getOctets());
        }
        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(1);
        if (this.iv != null) {
            v.add(this.iv);
        }
        return new DERSequence(v);
    }
}

