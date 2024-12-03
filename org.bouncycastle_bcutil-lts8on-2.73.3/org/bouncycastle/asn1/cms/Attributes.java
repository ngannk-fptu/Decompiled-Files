/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DLSet
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DLSet;
import org.bouncycastle.asn1.cms.Attribute;

public class Attributes
extends ASN1Object {
    private ASN1Set attributes;

    private Attributes(ASN1Set set) {
        this.attributes = set;
    }

    public Attributes(ASN1EncodableVector v) {
        this.attributes = new DLSet(v);
    }

    public static Attributes getInstance(Object obj) {
        if (obj instanceof Attributes) {
            return (Attributes)((Object)obj);
        }
        if (obj != null) {
            return new Attributes(ASN1Set.getInstance((Object)obj));
        }
        return null;
    }

    public static Attributes getInstance(ASN1TaggedObject obj, boolean explicit) {
        return Attributes.getInstance(ASN1Set.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public Attribute[] getAttributes() {
        Attribute[] rv = new Attribute[this.attributes.size()];
        for (int i = 0; i != rv.length; ++i) {
            rv[i] = Attribute.getInstance(this.attributes.getObjectAt(i));
        }
        return rv;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.attributes;
    }
}

