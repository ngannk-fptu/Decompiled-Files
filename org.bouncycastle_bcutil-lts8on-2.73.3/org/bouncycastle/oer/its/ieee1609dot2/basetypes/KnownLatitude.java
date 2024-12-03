/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.NinetyDegreeInt;

public class KnownLatitude
extends NinetyDegreeInt {
    public KnownLatitude(long value) {
        super(value);
    }

    public KnownLatitude(BigInteger value) {
        super(value);
    }

    private KnownLatitude(ASN1Integer integer) {
        this(integer.getValue());
    }

    public static KnownLatitude getInstance(Object o) {
        if (o instanceof KnownLatitude) {
            return (KnownLatitude)((Object)o);
        }
        if (o != null) {
            return new KnownLatitude(ASN1Integer.getInstance((Object)o));
        }
        return null;
    }
}

