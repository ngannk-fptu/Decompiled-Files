/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.asn1.eac;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;

public class UnsignedInteger
extends ASN1Object {
    private int tagNo;
    private BigInteger value;

    public UnsignedInteger(int tagNo, BigInteger value) {
        this.tagNo = tagNo;
        this.value = value;
    }

    private UnsignedInteger(ASN1TaggedObject obj) {
        this.tagNo = obj.getTagNo();
        this.value = new BigInteger(1, ASN1OctetString.getInstance((ASN1TaggedObject)obj, (boolean)false).getOctets());
    }

    public static UnsignedInteger getInstance(Object obj) {
        if (obj instanceof UnsignedInteger) {
            return (UnsignedInteger)((Object)obj);
        }
        if (obj != null) {
            return new UnsignedInteger(ASN1TaggedObject.getInstance((Object)obj));
        }
        return null;
    }

    private byte[] convertValue() {
        byte[] v = this.value.toByteArray();
        if (v[0] == 0) {
            byte[] tmp = new byte[v.length - 1];
            System.arraycopy(v, 1, tmp, 0, tmp.length);
            return tmp;
        }
        return v;
    }

    public int getTagNo() {
        return this.tagNo;
    }

    public BigInteger getValue() {
        return this.value;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(false, this.tagNo, (ASN1Encodable)new DEROctetString(this.convertValue()));
    }
}

