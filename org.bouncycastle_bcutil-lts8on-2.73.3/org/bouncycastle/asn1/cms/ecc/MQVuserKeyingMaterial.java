/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.asn1.cms.ecc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.OriginatorPublicKey;

public class MQVuserKeyingMaterial
extends ASN1Object {
    private OriginatorPublicKey ephemeralPublicKey;
    private ASN1OctetString addedukm;

    public MQVuserKeyingMaterial(OriginatorPublicKey ephemeralPublicKey, ASN1OctetString addedukm) {
        if (ephemeralPublicKey == null) {
            throw new IllegalArgumentException("Ephemeral public key cannot be null");
        }
        this.ephemeralPublicKey = ephemeralPublicKey;
        this.addedukm = addedukm;
    }

    private MQVuserKeyingMaterial(ASN1Sequence seq) {
        if (seq.size() != 1 && seq.size() != 2) {
            throw new IllegalArgumentException("Sequence has incorrect number of elements");
        }
        this.ephemeralPublicKey = OriginatorPublicKey.getInstance(seq.getObjectAt(0));
        if (seq.size() > 1) {
            this.addedukm = ASN1OctetString.getInstance((ASN1TaggedObject)((ASN1TaggedObject)seq.getObjectAt(1)), (boolean)true);
        }
    }

    public static MQVuserKeyingMaterial getInstance(ASN1TaggedObject obj, boolean explicit) {
        return MQVuserKeyingMaterial.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public static MQVuserKeyingMaterial getInstance(Object obj) {
        if (obj instanceof MQVuserKeyingMaterial) {
            return (MQVuserKeyingMaterial)((Object)obj);
        }
        if (obj != null) {
            return new MQVuserKeyingMaterial(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public OriginatorPublicKey getEphemeralPublicKey() {
        return this.ephemeralPublicKey;
    }

    public ASN1OctetString getAddedukm() {
        return this.addedukm;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.ephemeralPublicKey);
        if (this.addedukm != null) {
            v.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.addedukm));
        }
        return new DERSequence(v);
    }
}

