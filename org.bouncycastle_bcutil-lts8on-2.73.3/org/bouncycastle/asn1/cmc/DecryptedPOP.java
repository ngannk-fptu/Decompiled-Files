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
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmc.BodyPartID;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class DecryptedPOP
extends ASN1Object {
    private final BodyPartID bodyPartID;
    private final AlgorithmIdentifier thePOPAlgID;
    private final byte[] thePOP;

    public DecryptedPOP(BodyPartID bodyPartID, AlgorithmIdentifier thePOPAlgID, byte[] thePOP) {
        this.bodyPartID = bodyPartID;
        this.thePOPAlgID = thePOPAlgID;
        this.thePOP = Arrays.clone((byte[])thePOP);
    }

    private DecryptedPOP(ASN1Sequence seq) {
        if (seq.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.bodyPartID = BodyPartID.getInstance(seq.getObjectAt(0));
        this.thePOPAlgID = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(1));
        this.thePOP = Arrays.clone((byte[])ASN1OctetString.getInstance((Object)seq.getObjectAt(2)).getOctets());
    }

    public static DecryptedPOP getInstance(Object o) {
        if (o instanceof DecryptedPOP) {
            return (DecryptedPOP)((Object)o);
        }
        if (o != null) {
            return new DecryptedPOP(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public BodyPartID getBodyPartID() {
        return this.bodyPartID;
    }

    public AlgorithmIdentifier getThePOPAlgID() {
        return this.thePOPAlgID;
    }

    public byte[] getThePOP() {
        return Arrays.clone((byte[])this.thePOP);
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.bodyPartID);
        v.add((ASN1Encodable)this.thePOPAlgID);
        v.add((ASN1Encodable)new DEROctetString(this.thePOP));
        return new DERSequence(v);
    }
}

