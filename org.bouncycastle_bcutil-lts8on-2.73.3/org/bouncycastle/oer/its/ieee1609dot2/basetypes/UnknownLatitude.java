/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Latitude;

public class UnknownLatitude
extends Latitude {
    public static UnknownLatitude INSTANCE = new UnknownLatitude();

    private UnknownLatitude() {
        super(900000001L);
    }

    public static UnknownLatitude getInstance(Object o) {
        if (o instanceof UnknownLatitude) {
            return (UnknownLatitude)((Object)o);
        }
        if (o != null) {
            ASN1Integer integer = ASN1Integer.getInstance((Object)o);
            if (integer.getValue().intValue() != 900000001) {
                throw new IllegalArgumentException("value " + integer.getValue() + " is not unknown value of 900000001");
            }
            return INSTANCE;
        }
        return null;
    }
}

