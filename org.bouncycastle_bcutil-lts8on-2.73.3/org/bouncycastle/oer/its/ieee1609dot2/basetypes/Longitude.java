/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.OneEightyDegreeInt;

public class Longitude
extends OneEightyDegreeInt {
    public Longitude(long value) {
        super(value);
    }

    public Longitude(BigInteger value) {
        super(value);
    }

    private Longitude(ASN1Integer i) {
        this(i.getValue());
    }

    public static Longitude getInstance(Object o) {
        if (o instanceof Longitude) {
            return (Longitude)((Object)o);
        }
        if (o != null) {
            return new Longitude(ASN1Integer.getInstance((Object)o));
        }
        return null;
    }
}

