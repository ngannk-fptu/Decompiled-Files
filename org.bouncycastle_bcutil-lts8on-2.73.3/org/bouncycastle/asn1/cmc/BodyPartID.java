/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 */
package org.bouncycastle.asn1.cmc;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class BodyPartID
extends ASN1Object {
    public static final long bodyIdMax = 0xFFFFFFFFL;
    private final long id;

    public BodyPartID(long id) {
        if (id < 0L || id > 0xFFFFFFFFL) {
            throw new IllegalArgumentException("id out of range");
        }
        this.id = id;
    }

    private static long convert(BigInteger value) {
        if (value.bitLength() > 32) {
            throw new IllegalArgumentException("id out of range");
        }
        return value.longValue();
    }

    private BodyPartID(ASN1Integer id) {
        this(BodyPartID.convert(id.getValue()));
    }

    public static BodyPartID getInstance(Object o) {
        if (o instanceof BodyPartID) {
            return (BodyPartID)((Object)o);
        }
        if (o != null) {
            return new BodyPartID(ASN1Integer.getInstance((Object)o));
        }
        return null;
    }

    public long getID() {
        return this.id;
    }

    public ASN1Primitive toASN1Primitive() {
        return new ASN1Integer(this.id);
    }
}

