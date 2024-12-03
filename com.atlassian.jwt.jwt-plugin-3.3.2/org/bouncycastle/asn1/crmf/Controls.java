/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.crmf.AttributeTypeAndValue;

public class Controls
extends ASN1Object {
    private ASN1Sequence content;

    private Controls(ASN1Sequence aSN1Sequence) {
        this.content = aSN1Sequence;
    }

    public static Controls getInstance(Object object) {
        if (object instanceof Controls) {
            return (Controls)object;
        }
        if (object != null) {
            return new Controls(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public Controls(AttributeTypeAndValue attributeTypeAndValue) {
        this.content = new DERSequence(attributeTypeAndValue);
    }

    public Controls(AttributeTypeAndValue[] attributeTypeAndValueArray) {
        this.content = new DERSequence(attributeTypeAndValueArray);
    }

    public AttributeTypeAndValue[] toAttributeTypeAndValueArray() {
        AttributeTypeAndValue[] attributeTypeAndValueArray = new AttributeTypeAndValue[this.content.size()];
        for (int i = 0; i != attributeTypeAndValueArray.length; ++i) {
            attributeTypeAndValueArray[i] = AttributeTypeAndValue.getInstance(this.content.getObjectAt(i));
        }
        return attributeTypeAndValueArray;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}

