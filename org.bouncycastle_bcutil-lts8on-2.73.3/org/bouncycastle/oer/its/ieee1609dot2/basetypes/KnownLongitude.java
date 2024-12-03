/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Longitude;

public class KnownLongitude
extends Longitude {
    public KnownLongitude(long value) {
        super(value);
    }

    public KnownLongitude(BigInteger value) {
        super(value);
    }

    private KnownLongitude(ASN1Integer integer) {
        this(integer.getValue());
    }

    public static KnownLongitude getInstance(Object o) {
        if (o instanceof KnownLongitude) {
            return (KnownLongitude)((Object)o);
        }
        if (o != null) {
            return new KnownLongitude(ASN1Integer.getInstance((Object)o));
        }
        return null;
    }
}

