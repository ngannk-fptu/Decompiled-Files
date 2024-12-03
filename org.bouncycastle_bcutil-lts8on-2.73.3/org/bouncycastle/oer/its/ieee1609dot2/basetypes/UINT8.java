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

public class UINT8
extends UintBase {
    private static final BigInteger MAX = BigInteger.valueOf(255L);

    public UINT8(BigInteger value) {
        super(value);
    }

    public UINT8(int value) {
        super(value);
    }

    public UINT8(long value) {
        super(value);
    }

    protected UINT8(ASN1Integer integer) {
        super(integer);
    }

    public static UINT8 getInstance(Object o) {
        if (o instanceof UINT8) {
            return (UINT8)((Object)o);
        }
        if (o != null) {
            return new UINT8(ASN1Integer.getInstance((Object)o));
        }
        return null;
    }

    @Override
    protected void assertLimit() {
        if (this.value.signum() < 0) {
            throw new IllegalArgumentException("value must not be negative");
        }
        if (this.value.compareTo(MAX) > 0) {
            throw new IllegalArgumentException("value 0x" + this.value.toString(16) + "  must not exceed 0x" + MAX.toString(16));
        }
    }
}

