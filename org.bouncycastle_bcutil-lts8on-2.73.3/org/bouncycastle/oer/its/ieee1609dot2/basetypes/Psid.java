/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class Psid
extends ASN1Object {
    private final BigInteger psid;

    public Psid(long psid) {
        this(BigInteger.valueOf(psid));
    }

    public Psid(BigInteger psid) {
        if (psid.signum() < 0) {
            throw new IllegalStateException("psid must be greater than zero");
        }
        this.psid = psid;
    }

    public BigInteger getPsid() {
        return this.psid;
    }

    public ASN1Primitive toASN1Primitive() {
        return new ASN1Integer(this.psid);
    }

    public static Psid getInstance(Object o) {
        if (o instanceof Psid) {
            return (Psid)((Object)o);
        }
        if (o != null) {
            return new Psid(ASN1Integer.getInstance((Object)o).getValue());
        }
        return null;
    }
}

