/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.util.BigIntegers
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT64;
import org.bouncycastle.util.BigIntegers;

public class Time64
extends UINT64 {
    public static long etsiEpochMicros = Time32.etsiEpochMillis * 1000L;

    public Time64(long value) {
        this(BigInteger.valueOf(value));
    }

    public Time64(BigInteger value) {
        super(value);
    }

    public Time64(UINT64 uint64) {
        this(uint64.getValue());
    }

    public static Time64 now() {
        return new Time64(1000L * System.currentTimeMillis() - etsiEpochMicros);
    }

    public static Time64 ofUnixMillis(long unixMillis) {
        return new Time64(unixMillis * 1000L - etsiEpochMicros);
    }

    public static Time64 getInstance(Object o) {
        if (o instanceof UINT64) {
            return new Time64((UINT64)((Object)o));
        }
        if (o != null) {
            return new Time64(ASN1Integer.getInstance((Object)o).getValue());
        }
        return null;
    }

    public long toUnixMillis() {
        return (BigIntegers.longValueExact((BigInteger)this.getValue()) + etsiEpochMicros) / 1000L;
    }
}

