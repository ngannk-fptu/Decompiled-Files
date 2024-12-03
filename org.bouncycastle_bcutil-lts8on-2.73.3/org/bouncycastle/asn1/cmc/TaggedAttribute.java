/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmc.BodyPartID;

public class TaggedAttribute
extends ASN1Object {
    private final BodyPartID bodyPartID;
    private final ASN1ObjectIdentifier attrType;
    private final ASN1Set attrValues;

    public static TaggedAttribute getInstance(Object o) {
        if (o instanceof TaggedAttribute) {
            return (TaggedAttribute)((Object)o);
        }
        if (o != null) {
            return new TaggedAttribute(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    private TaggedAttribute(ASN1Sequence seq) {
        if (seq.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.bodyPartID = BodyPartID.getInstance(seq.getObjectAt(0));
        this.attrType = ASN1ObjectIdentifier.getInstance((Object)seq.getObjectAt(1));
        this.attrValues = ASN1Set.getInstance((Object)seq.getObjectAt(2));
    }

    public TaggedAttribute(BodyPartID bodyPartID, ASN1ObjectIdentifier attrType, ASN1Set attrValues) {
        this.bodyPartID = bodyPartID;
        this.attrType = attrType;
        this.attrValues = attrValues;
    }

    public BodyPartID getBodyPartID() {
        return this.bodyPartID;
    }

    public ASN1ObjectIdentifier getAttrType() {
        return this.attrType;
    }

    public ASN1Set getAttrValues() {
        return this.attrValues;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.bodyPartID, this.attrType, this.attrValues});
    }
}

