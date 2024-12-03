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
package org.bouncycastle.asn1.est;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.est.AttrOrOID;
import org.bouncycastle.asn1.est.Utils;

public class CsrAttrs
extends ASN1Object {
    private final AttrOrOID[] attrOrOIDs;

    public static CsrAttrs getInstance(Object obj) {
        if (obj instanceof CsrAttrs) {
            return (CsrAttrs)((Object)obj);
        }
        if (obj != null) {
            return new CsrAttrs(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public static CsrAttrs getInstance(ASN1TaggedObject obj, boolean explicit) {
        return CsrAttrs.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public CsrAttrs(AttrOrOID attrOrOID) {
        this.attrOrOIDs = new AttrOrOID[]{attrOrOID};
    }

    public CsrAttrs(AttrOrOID[] attrOrOIDs) {
        this.attrOrOIDs = Utils.clone(attrOrOIDs);
    }

    private CsrAttrs(ASN1Sequence seq) {
        this.attrOrOIDs = new AttrOrOID[seq.size()];
        for (int i = 0; i != seq.size(); ++i) {
            this.attrOrOIDs[i] = AttrOrOID.getInstance(seq.getObjectAt(i));
        }
    }

    public AttrOrOID[] getAttrOrOIDs() {
        return Utils.clone(this.attrOrOIDs);
    }

    public int size() {
        return this.attrOrOIDs.length;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence((ASN1Encodable[])this.attrOrOIDs);
    }
}

