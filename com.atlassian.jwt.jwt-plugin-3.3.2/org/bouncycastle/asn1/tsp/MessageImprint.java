/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class MessageImprint
extends ASN1Object {
    AlgorithmIdentifier hashAlgorithm;
    byte[] hashedMessage;

    public static MessageImprint getInstance(Object object) {
        if (object instanceof MessageImprint) {
            return (MessageImprint)object;
        }
        if (object != null) {
            return new MessageImprint(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    private MessageImprint(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("sequence has wrong number of elements");
        }
        this.hashAlgorithm = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(0));
        this.hashedMessage = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1)).getOctets();
    }

    public MessageImprint(AlgorithmIdentifier algorithmIdentifier, byte[] byArray) {
        this.hashAlgorithm = algorithmIdentifier;
        this.hashedMessage = Arrays.clone(byArray);
    }

    public AlgorithmIdentifier getHashAlgorithm() {
        return this.hashAlgorithm;
    }

    public byte[] getHashedMessage() {
        return Arrays.clone(this.hashedMessage);
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(2);
        aSN1EncodableVector.add(this.hashAlgorithm);
        aSN1EncodableVector.add(new DEROctetString(this.hashedMessage));
        return new DERSequence(aSN1EncodableVector);
    }
}

