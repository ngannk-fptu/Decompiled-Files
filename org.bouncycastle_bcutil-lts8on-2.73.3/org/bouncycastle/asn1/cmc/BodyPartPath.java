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
package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmc.BodyPartID;
import org.bouncycastle.asn1.cmc.Utils;

public class BodyPartPath
extends ASN1Object {
    private final BodyPartID[] bodyPartIDs;

    public static BodyPartPath getInstance(Object obj) {
        if (obj instanceof BodyPartPath) {
            return (BodyPartPath)((Object)obj);
        }
        if (obj != null) {
            return new BodyPartPath(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public static BodyPartPath getInstance(ASN1TaggedObject obj, boolean explicit) {
        return BodyPartPath.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public BodyPartPath(BodyPartID bodyPartID) {
        this.bodyPartIDs = new BodyPartID[]{bodyPartID};
    }

    public BodyPartPath(BodyPartID[] bodyPartIDs) {
        this.bodyPartIDs = Utils.clone(bodyPartIDs);
    }

    private BodyPartPath(ASN1Sequence seq) {
        this.bodyPartIDs = Utils.toBodyPartIDArray(seq);
    }

    public BodyPartID[] getBodyPartIDs() {
        return Utils.clone(this.bodyPartIDs);
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence((ASN1Encodable[])this.bodyPartIDs);
    }
}

