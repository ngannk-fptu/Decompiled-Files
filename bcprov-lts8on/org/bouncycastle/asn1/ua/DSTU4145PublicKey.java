/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.ua;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ua.DSTU4145PointEncoder;
import org.bouncycastle.math.ec.ECPoint;

public class DSTU4145PublicKey
extends ASN1Object {
    private ASN1OctetString pubKey;

    public DSTU4145PublicKey(ECPoint pubKey) {
        this.pubKey = new DEROctetString(DSTU4145PointEncoder.encodePoint(pubKey));
    }

    private DSTU4145PublicKey(ASN1OctetString ocStr) {
        this.pubKey = ocStr;
    }

    public static DSTU4145PublicKey getInstance(Object obj) {
        if (obj instanceof DSTU4145PublicKey) {
            return (DSTU4145PublicKey)obj;
        }
        if (obj != null) {
            return new DSTU4145PublicKey(ASN1OctetString.getInstance(obj));
        }
        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.pubKey;
    }
}

