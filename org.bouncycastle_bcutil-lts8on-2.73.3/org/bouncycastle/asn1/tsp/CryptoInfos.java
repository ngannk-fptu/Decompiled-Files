/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.Attribute;

public class CryptoInfos
extends ASN1Object {
    private ASN1Sequence attributes;

    public static CryptoInfos getInstance(Object obj) {
        if (obj instanceof CryptoInfos) {
            return (CryptoInfos)((Object)obj);
        }
        if (obj != null) {
            return new CryptoInfos(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public static CryptoInfos getInstance(ASN1TaggedObject obj, boolean explicit) {
        return CryptoInfos.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    private CryptoInfos(ASN1Sequence attributes) {
        this.attributes = attributes;
    }

    public CryptoInfos(Attribute[] attrs) {
        this.attributes = new DERSequence((ASN1Encodable[])attrs);
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

