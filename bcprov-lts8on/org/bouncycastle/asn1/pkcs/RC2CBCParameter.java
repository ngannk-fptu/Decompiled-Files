/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.pkcs;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;

public class RC2CBCParameter
extends ASN1Object {
    ASN1Integer version;
    ASN1OctetString iv;

    public static RC2CBCParameter getInstance(Object o) {
        if (o instanceof RC2CBCParameter) {
            return (RC2CBCParameter)o;
        }
        if (o != null) {
            return new RC2CBCParameter(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public RC2CBCParameter(byte[] iv) {
        this.version = null;
        this.iv = new DEROctetString(iv);
    }

    public RC2CBCParameter(int parameterVersion, byte[] iv) {
        this.version = new ASN1Integer(parameterVersion);
        this.iv = new DEROctetString(iv);
    }

    private RC2CBCParameter(ASN1Sequence seq) {
        if (seq.size() == 1) {
            this.version = null;
            this.iv = (ASN1OctetString)seq.getObjectAt(0);
        } else {
            this.version = (ASN1Integer)seq.getObjectAt(0);
            this.iv = (ASN1OctetString)seq.getObjectAt(1);
        }
    }

    public BigInteger getRC2ParameterVersion() {
        if (this.version == null) {
            return null;
        }
        return this.version.getValue();
    }

    public byte[] getIV() {
        return this.iv.getOctets();
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        if (this.version != null) {
            v.add(this.version);
        }
        v.add(this.iv);
        return new DERSequence(v);
    }
}

