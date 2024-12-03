/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1BitString
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.DERBitString
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERBitString;

public class EndEntityType
extends ASN1Object {
    public static final int app = 128;
    public static final int enrol = 64;
    private final ASN1BitString type;

    public EndEntityType(int eeType) {
        this((ASN1BitString)new DERBitString(eeType));
    }

    private EndEntityType(ASN1BitString str) {
        this.type = str;
    }

    public static EndEntityType getInstance(Object src) {
        if (src instanceof EndEntityType) {
            return (EndEntityType)((Object)src);
        }
        if (src != null) {
            return new EndEntityType(ASN1BitString.getInstance((Object)src));
        }
        return null;
    }

    public ASN1BitString getType() {
        return this.type;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.type;
    }
}

