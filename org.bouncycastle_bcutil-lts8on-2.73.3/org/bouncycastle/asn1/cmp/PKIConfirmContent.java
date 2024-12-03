/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Null
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.DERNull
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;

public class PKIConfirmContent
extends ASN1Object {
    private final ASN1Null val;

    private PKIConfirmContent(ASN1Null val) {
        this.val = val;
    }

    public PKIConfirmContent() {
        this.val = DERNull.INSTANCE;
    }

    public static PKIConfirmContent getInstance(Object o) {
        if (o == null || o instanceof PKIConfirmContent) {
            return (PKIConfirmContent)((Object)o);
        }
        if (o instanceof ASN1Null) {
            return new PKIConfirmContent((ASN1Null)o);
        }
        throw new IllegalArgumentException("Invalid object: " + o.getClass().getName());
    }

    public ASN1Primitive toASN1Primitive() {
        return this.val;
    }
}

