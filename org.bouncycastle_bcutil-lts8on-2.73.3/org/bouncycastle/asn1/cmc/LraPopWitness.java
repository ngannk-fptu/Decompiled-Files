/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmc.BodyPartID;

public class LraPopWitness
extends ASN1Object {
    private final BodyPartID pkiDataBodyid;
    private final ASN1Sequence bodyIds;

    public LraPopWitness(BodyPartID pkiDataBodyid, ASN1Sequence bodyIds) {
        this.pkiDataBodyid = pkiDataBodyid;
        this.bodyIds = bodyIds;
    }

    private LraPopWitness(ASN1Sequence seq) {
        if (seq.size() != 2) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.pkiDataBodyid = BodyPartID.getInstance(seq.getObjectAt(0));
        this.bodyIds = ASN1Sequence.getInstance((Object)seq.getObjectAt(1));
    }

    public static LraPopWitness getInstance(Object o) {
        if (o instanceof LraPopWitness) {
            return (LraPopWitness)((Object)o);
        }
        if (o != null) {
            return new LraPopWitness(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public BodyPartID getPkiDataBodyid() {
        return this.pkiDataBodyid;
    }

    public BodyPartID[] getBodyIds() {
        BodyPartID[] rv = new BodyPartID[this.bodyIds.size()];
        for (int i = 0; i != this.bodyIds.size(); ++i) {
            rv[i] = BodyPartID.getInstance(this.bodyIds.getObjectAt(i));
        }
        return rv;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.pkiDataBodyid);
        v.add((ASN1Encodable)this.bodyIds);
        return new DERSequence(v);
    }
}

