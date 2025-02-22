/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x9;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;

public class DHPublicKey
extends ASN1Object {
    private ASN1Integer y;

    public static DHPublicKey getInstance(ASN1TaggedObject obj, boolean explicit) {
        return DHPublicKey.getInstance(ASN1Integer.getInstance(obj, explicit));
    }

    public static DHPublicKey getInstance(Object obj) {
        if (obj == null || obj instanceof DHPublicKey) {
            return (DHPublicKey)obj;
        }
        if (obj instanceof ASN1Integer) {
            return new DHPublicKey((ASN1Integer)obj);
        }
        throw new IllegalArgumentException("Invalid DHPublicKey: " + obj.getClass().getName());
    }

    private DHPublicKey(ASN1Integer y) {
        if (y == null) {
            throw new IllegalArgumentException("'y' cannot be null");
        }
        this.y = y;
    }

    public DHPublicKey(BigInteger y) {
        if (y == null) {
            throw new IllegalArgumentException("'y' cannot be null");
        }
        this.y = new ASN1Integer(y);
    }

    public BigInteger getY() {
        return this.y.getPositiveValue();
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.y;
    }
}

