/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmc.BodyPartID;
import org.bouncycastle.asn1.cmc.CertificationRequest;

public class TaggedCertificationRequest
extends ASN1Object {
    private final BodyPartID bodyPartID;
    private final CertificationRequest certificationRequest;

    public TaggedCertificationRequest(BodyPartID bodyPartID, CertificationRequest certificationRequest) {
        this.bodyPartID = bodyPartID;
        this.certificationRequest = certificationRequest;
    }

    private TaggedCertificationRequest(ASN1Sequence seq) {
        if (seq.size() != 2) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.bodyPartID = BodyPartID.getInstance(seq.getObjectAt(0));
        this.certificationRequest = CertificationRequest.getInstance(seq.getObjectAt(1));
    }

    public static TaggedCertificationRequest getInstance(Object o) {
        if (o instanceof TaggedCertificationRequest) {
            return (TaggedCertificationRequest)((Object)o);
        }
        if (o != null) {
            return new TaggedCertificationRequest(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public static TaggedCertificationRequest getInstance(ASN1TaggedObject obj, boolean explicit) {
        return TaggedCertificationRequest.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public BodyPartID getBodyPartID() {
        return this.bodyPartID;
    }

    public CertificationRequest getCertificationRequest() {
        return this.certificationRequest;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.bodyPartID);
        v.add((ASN1Encodable)this.certificationRequest);
        return new DERSequence(v);
    }
}

