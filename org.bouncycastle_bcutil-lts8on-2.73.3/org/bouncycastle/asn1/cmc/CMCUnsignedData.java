/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmc.BodyPartPath;

public class CMCUnsignedData
extends ASN1Object {
    private final BodyPartPath bodyPartPath;
    private final ASN1ObjectIdentifier identifier;
    private final ASN1Encodable content;

    public CMCUnsignedData(BodyPartPath bodyPartPath, ASN1ObjectIdentifier identifier, ASN1Encodable content) {
        this.bodyPartPath = bodyPartPath;
        this.identifier = identifier;
        this.content = content;
    }

    private CMCUnsignedData(ASN1Sequence seq) {
        if (seq.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.bodyPartPath = BodyPartPath.getInstance(seq.getObjectAt(0));
        this.identifier = ASN1ObjectIdentifier.getInstance((Object)seq.getObjectAt(1));
        this.content = seq.getObjectAt(2);
    }

    public static CMCUnsignedData getInstance(Object o) {
        if (o instanceof CMCUnsignedData) {
            return (CMCUnsignedData)((Object)o);
        }
        if (o != null) {
            return new CMCUnsignedData(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.bodyPartPath);
        v.add((ASN1Encodable)this.identifier);
        v.add(this.content);
        return new DERSequence(v);
    }

    public BodyPartPath getBodyPartPath() {
        return this.bodyPartPath;
    }

    public ASN1ObjectIdentifier getIdentifier() {
        return this.identifier;
    }

    public ASN1Encodable getContent() {
        return this.content;
    }
}

