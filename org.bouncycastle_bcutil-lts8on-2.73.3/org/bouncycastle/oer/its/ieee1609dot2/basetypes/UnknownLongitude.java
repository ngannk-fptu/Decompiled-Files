/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Longitude;

public class UnknownLongitude
extends Longitude {
    public static final UnknownLongitude INSTANCE = new UnknownLongitude();

    public UnknownLongitude() {
        super(1800000001L);
    }

    public static UnknownLongitude getInstance(Object o) {
        if (o instanceof UnknownLongitude) {
            return (UnknownLongitude)((Object)o);
        }
        if (o != null) {
            ASN1Integer integer = ASN1Integer.getInstance((Object)o);
            if (integer.getValue().intValue() != 1800000001) {
                throw new IllegalArgumentException("value " + integer.getValue() + " is not 1800000001");
            }
            return INSTANCE;
        }
        return null;
    }
}

