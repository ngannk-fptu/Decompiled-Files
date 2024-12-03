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
package org.bouncycastle.asn1.tsp;

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

public class MessageImprint
extends ASN1Object {
    AlgorithmIdentifier hashAlgorithm;
    byte[] hashedMessage;

    public static MessageImprint getInstance(Object o) {
        if (o instanceof MessageImprint) {
            return (MessageImprint)((Object)o);
        }
        if (o != null) {
            return new MessageImprint(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    private MessageImprint(ASN1Sequence seq) {
        if (seq.size() != 2) {
            throw new IllegalArgumentException("sequence has wrong number of elements");
        }
        this.hashAlgorithm = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(0));
        this.hashedMessage = ASN1OctetString.getInstance((Object)seq.getObjectAt(1)).getOctets();
    }

    public MessageImprint(AlgorithmIdentifier hashAlgorithm, byte[] hashedMessage) {
        this.hashAlgorithm = hashAlgorithm;
        this.hashedMessage = Arrays.clone((byte[])hashedMessage);
    }

    public AlgorithmIdentifier getHashAlgorithm() {
        return this.hashAlgorithm;
    }

    public byte[] getHashedMessage() {
        return Arrays.clone((byte[])this.hashedMessage);
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.hashAlgorithm);
        v.add((ASN1Encodable)new DEROctetString(this.hashedMessage));
        return new DERSequence(v);
    }
}

