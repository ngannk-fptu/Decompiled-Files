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
import org.bouncycastle.asn1.crmf.PKIPublicationInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class CMCPublicationInfo
extends ASN1Object {
    private final AlgorithmIdentifier hashAlg;
    private final ASN1Sequence certHashes;
    private final PKIPublicationInfo pubInfo;

    public CMCPublicationInfo(AlgorithmIdentifier hashAlg, byte[][] anchorHashes, PKIPublicationInfo pubInfo) {
        this.hashAlg = hashAlg;
        ASN1EncodableVector v = new ASN1EncodableVector(anchorHashes.length);
        for (int i = 0; i != anchorHashes.length; ++i) {
            v.add((ASN1Encodable)new DEROctetString(Arrays.clone((byte[])anchorHashes[i])));
        }
        this.certHashes = new DERSequence(v);
        this.pubInfo = pubInfo;
    }

    private CMCPublicationInfo(ASN1Sequence seq) {
        if (seq.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.hashAlg = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(0));
        this.certHashes = ASN1Sequence.getInstance((Object)seq.getObjectAt(1));
        this.pubInfo = PKIPublicationInfo.getInstance(seq.getObjectAt(2));
    }

    public static CMCPublicationInfo getInstance(Object o) {
        if (o instanceof CMCPublicationInfo) {
            return (CMCPublicationInfo)((Object)o);
        }
        if (o != null) {
            return new CMCPublicationInfo(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public AlgorithmIdentifier getHashAlg() {
        return this.hashAlg;
    }

    public byte[][] getCertHashes() {
        byte[][] hashes = new byte[this.certHashes.size()][];
        for (int i = 0; i != hashes.length; ++i) {
            hashes[i] = Arrays.clone((byte[])ASN1OctetString.getInstance((Object)this.certHashes.getObjectAt(i)).getOctets());
        }
        return hashes;
    }

    public PKIPublicationInfo getPubInfo() {
        return this.pubInfo;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.hashAlg);
        v.add((ASN1Encodable)this.certHashes);
        v.add((ASN1Encodable)this.pubInfo);
        return new DERSequence(v);
    }
}

