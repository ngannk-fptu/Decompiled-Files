/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT16;

public class Elevation
extends UINT16 {
    public Elevation(UINT16 value) {
        super(value.getValue());
    }

    public Elevation(BigInteger value) {
        super(value);
    }

    public Elevation(int value) {
        super(value);
    }

    public Elevation(long value) {
        super(value);
    }

    protected Elevation(ASN1Integer integer) {
        super(integer);
    }

    public static Elevation getInstance(Object o) {
        if (o instanceof Elevation) {
            return (Elevation)((Object)o);
        }
        if (o != null) {
            return new Elevation(UINT16.getInstance(o));
        }
        return null;
    }
}

