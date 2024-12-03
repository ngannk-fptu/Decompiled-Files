/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.util.BigIntegers
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.BigIntegers;

public class IValue
extends ASN1Object {
    private final BigInteger value;

    private IValue(ASN1Integer value) {
        int i = BigIntegers.intValueExact((BigInteger)value.getValue());
        if (i < 0 || i > 65535) {
            throw new IllegalArgumentException("value out of range");
        }
        this.value = value.getValue();
    }

    public static IValue getInstance(Object src) {
        if (src instanceof IValue) {
            return (IValue)((Object)src);
        }
        if (src != null) {
            return new IValue(ASN1Integer.getInstance((Object)src));
        }
        return null;
    }

    public BigInteger getValue() {
        return this.value;
    }

    public ASN1Primitive toASN1Primitive() {
        return new ASN1Integer(this.value);
    }
}

