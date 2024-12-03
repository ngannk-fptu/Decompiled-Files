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
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class GenericHybridParameters
extends ASN1Object {
    private final AlgorithmIdentifier kem;
    private final AlgorithmIdentifier dem;

    private GenericHybridParameters(ASN1Sequence sequence) {
        if (sequence.size() != 2) {
            throw new IllegalArgumentException("ASN.1 SEQUENCE should be of length 2");
        }
        this.kem = AlgorithmIdentifier.getInstance((Object)sequence.getObjectAt(0));
        this.dem = AlgorithmIdentifier.getInstance((Object)sequence.getObjectAt(1));
    }

    public static GenericHybridParameters getInstance(Object o) {
        if (o instanceof GenericHybridParameters) {
            return (GenericHybridParameters)((Object)o);
        }
        if (o != null) {
            return new GenericHybridParameters(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public GenericHybridParameters(AlgorithmIdentifier kem, AlgorithmIdentifier dem) {
        this.kem = kem;
        this.dem = dem;
    }

    public AlgorithmIdentifier getDem() {
        return this.dem;
    }

    public AlgorithmIdentifier getKem() {
        return this.kem;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.kem);
        v.add((ASN1Encodable)this.dem);
        return new DERSequence(v);
    }
}

