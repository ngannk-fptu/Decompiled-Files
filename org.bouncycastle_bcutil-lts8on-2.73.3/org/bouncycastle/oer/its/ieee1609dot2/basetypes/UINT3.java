/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UintBase;

public class UINT3
extends UintBase {
    private static final BigInteger MAX = BigInteger.valueOf(7L);

    public UINT3(BigInteger value) {
        super(value);
    }

    public UINT3(int value) {
        super(value);
    }

    public UINT3(long value) {
        super(value);
    }

    protected UINT3(ASN1Integer integer) {
        super(integer);
    }

    public static UINT3 getInstance(Object o) {
        if (o instanceof UINT3) {
            return (UINT3)((Object)o);
        }
        if (o != null) {
            return new UINT3(ASN1Integer.getInstance((Object)o));
        }
        return null;
    }

    @Override
    protected void assertLimit() {
        if (this.value.signum() < 0) {
            throw new IllegalArgumentException("value must not be negative");
        }
        if (this.value.compareTo(MAX) > 0) {
            throw new IllegalArgumentException("value must not exceed " + MAX.toString(16));
        }
    }
}

