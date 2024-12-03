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
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class PopLinkWitnessV2
extends ASN1Object {
    private final AlgorithmIdentifier keyGenAlgorithm;
    private final AlgorithmIdentifier macAlgorithm;
    private final byte[] witness;

    public PopLinkWitnessV2(AlgorithmIdentifier keyGenAlgorithm, AlgorithmIdentifier macAlgorithm, byte[] witness) {
        this.keyGenAlgorithm = keyGenAlgorithm;
        this.macAlgorithm = macAlgorithm;
        this.witness = Arrays.clone((byte[])witness);
    }

    private PopLinkWitnessV2(ASN1Sequence seq) {
        if (seq.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.keyGenAlgorithm = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(0));
        this.macAlgorithm = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(1));
        this.witness = Arrays.clone((byte[])ASN1OctetString.getInstance((Object)seq.getObjectAt(2)).getOctets());
    }

    public static PopLinkWitnessV2 getInstance(Object o) {
        if (o instanceof PopLinkWitnessV2) {
            return (PopLinkWitnessV2)((Object)o);
        }
        if (o != null) {
            return new PopLinkWitnessV2(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public AlgorithmIdentifier getKeyGenAlgorithm() {
        return this.keyGenAlgorithm;
    }

    public AlgorithmIdentifier getMacAlgorithm() {
        return this.macAlgorithm;
    }

    public byte[] getWitness() {
        return Arrays.clone((byte[])this.witness);
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.keyGenAlgorithm);
        v.add((ASN1Encodable)this.macAlgorithm);
        v.add((ASN1Encodable)new DEROctetString(this.getWitness()));
        return new DERSequence(v);
    }
}

