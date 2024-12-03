/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.asn1.isismtt.ocsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class CertHash
extends ASN1Object {
    private AlgorithmIdentifier hashAlgorithm;
    private byte[] certificateHash;

    public static CertHash getInstance(Object obj) {
        if (obj == null || obj instanceof CertHash) {
            return (CertHash)((Object)obj);
        }
        if (obj instanceof ASN1Sequence) {
            return new CertHash((ASN1Sequence)obj);
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    private CertHash(ASN1Sequence seq) {
        if (seq.size() != 2) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        this.hashAlgorithm = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(0));
        this.certificateHash = DEROctetString.getInstance((Object)seq.getObjectAt(1)).getOctets();
    }

    public CertHash(AlgorithmIdentifier hashAlgorithm, byte[] certificateHash) {
        this.hashAlgorithm = hashAlgorithm;
        this.certificateHash = new byte[certificateHash.length];
        System.arraycopy(certificateHash, 0, this.certificateHash, 0, certificateHash.length);
    }

    public AlgorithmIdentifier getHashAlgorithm() {
        return this.hashAlgorithm;
    }

    public byte[] getCertificateHash() {
        return Arrays.clone((byte[])this.certificateHash);
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector vec = new ASN1EncodableVector(2);
        vec.add((ASN1Encodable)this.hashAlgorithm);
        vec.add((ASN1Encodable)new DEROctetString(this.certificateHash));
        return new DERSequence(vec);
    }
}

