/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
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

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class PublishTrustAnchors
extends ASN1Object {
    private final ASN1Integer seqNumber;
    private final AlgorithmIdentifier hashAlgorithm;
    private final ASN1Sequence anchorHashes;

    public PublishTrustAnchors(BigInteger seqNumber, AlgorithmIdentifier hashAlgorithm, byte[][] anchorHashes) {
        this.seqNumber = new ASN1Integer(seqNumber);
        this.hashAlgorithm = hashAlgorithm;
        ASN1EncodableVector v = new ASN1EncodableVector(anchorHashes.length);
        for (int i = 0; i != anchorHashes.length; ++i) {
            v.add((ASN1Encodable)new DEROctetString(Arrays.clone((byte[])anchorHashes[i])));
        }
        this.anchorHashes = new DERSequence(v);
    }

    private PublishTrustAnchors(ASN1Sequence seq) {
        if (seq.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.seqNumber = ASN1Integer.getInstance((Object)seq.getObjectAt(0));
        this.hashAlgorithm = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(1));
        this.anchorHashes = ASN1Sequence.getInstance((Object)seq.getObjectAt(2));
    }

    public static PublishTrustAnchors getInstance(Object o) {
        if (o instanceof PublishTrustAnchors) {
            return (PublishTrustAnchors)((Object)o);
        }
        if (o != null) {
            return new PublishTrustAnchors(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public BigInteger getSeqNumber() {
        return this.seqNumber.getValue();
    }

    public AlgorithmIdentifier getHashAlgorithm() {
        return this.hashAlgorithm;
    }

    public byte[][] getAnchorHashes() {
        byte[][] hashes = new byte[this.anchorHashes.size()][];
        for (int i = 0; i != hashes.length; ++i) {
            hashes[i] = Arrays.clone((byte[])ASN1OctetString.getInstance((Object)this.anchorHashes.getObjectAt(i)).getOctets());
        }
        return hashes;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.seqNumber);
        v.add((ASN1Encodable)this.hashAlgorithm);
        v.add((ASN1Encodable)this.anchorHashes);
        return new DERSequence(v);
    }
}

