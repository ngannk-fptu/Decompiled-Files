/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.x500.DirectoryString
 */
package org.bouncycastle.asn1.isismtt.x509;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.DirectoryString;

public class Restriction
extends ASN1Object {
    private DirectoryString restriction;

    public static Restriction getInstance(Object obj) {
        if (obj instanceof Restriction) {
            return (Restriction)((Object)obj);
        }
        if (obj != null) {
            return new Restriction(DirectoryString.getInstance((Object)obj));
        }
        return null;
    }

    private Restriction(DirectoryString restriction) {
        this.restriction = restriction;
    }

    public Restriction(String restriction) {
        this.restriction = new DirectoryString(restriction);
    }

    public DirectoryString getRestriction() {
        return this.restriction;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.restriction.toASN1Primitive();
    }
}

