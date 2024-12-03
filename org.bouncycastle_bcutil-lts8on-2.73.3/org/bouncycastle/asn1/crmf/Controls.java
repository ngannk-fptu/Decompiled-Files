/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.crmf.AttributeTypeAndValue;

public class Controls
extends ASN1Object {
    private ASN1Sequence content;

    private Controls(ASN1Sequence seq) {
        this.content = seq;
    }

    public static Controls getInstance(Object o) {
        if (o instanceof Controls) {
            return (Controls)((Object)o);
        }
        if (o != null) {
            return new Controls(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public Controls(AttributeTypeAndValue atv) {
        this.content = new DERSequence((ASN1Encodable)atv);
    }

    public Controls(AttributeTypeAndValue[] atvs) {
        this.content = new DERSequence((ASN1Encodable[])atvs);
    }

    public AttributeTypeAndValue[] toAttributeTypeAndValueArray() {
        AttributeTypeAndValue[] result = new AttributeTypeAndValue[this.content.size()];
        for (int i = 0; i != result.length; ++i) {
            result[i] = AttributeTypeAndValue.getInstance(this.content.getObjectAt(i));
        }
        return result;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}

