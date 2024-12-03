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

public class CrlSeries
extends UINT16 {
    public CrlSeries(int value) {
        super(value);
    }

    public CrlSeries(BigInteger value) {
        super(value);
    }

    public static CrlSeries getInstance(Object o) {
        if (o instanceof CrlSeries) {
            return (CrlSeries)((Object)o);
        }
        if (o != null) {
            return new CrlSeries(ASN1Integer.getInstance((Object)o).getValue());
        }
        return null;
    }
}

