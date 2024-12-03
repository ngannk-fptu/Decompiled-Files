/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import java.util.Date;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT32;

public class Time32
extends UINT32 {
    public static long etsiEpochMillis = 1072915200000L;

    public Time32(long value) {
        super(value);
    }

    public Time32(BigInteger value) {
        super(value);
    }

    public Time32(UINT32 uint32) {
        this(uint32.getValue());
    }

    public static Time32 now() {
        return Time32.ofUnixMillis(System.currentTimeMillis());
    }

    public static Time32 ofUnixMillis(long unixMillis) {
        return new Time32((unixMillis - etsiEpochMillis) / 1000L);
    }

    public static Time32 getInstance(Object o) {
        if (o instanceof UINT32) {
            return new Time32((UINT32)((Object)o));
        }
        if (o != null) {
            return new Time32(ASN1Integer.getInstance((Object)o).getValue());
        }
        return null;
    }

    public long toUnixMillis() {
        return this.getValue().longValue() * 1000L + etsiEpochMillis;
    }

    public String toString() {
        return new Date(this.toUnixMillis()).toString();
    }
}

