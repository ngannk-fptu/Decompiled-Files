/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class DigestInfo
extends ASN1Object {
    private byte[] digest;
    private AlgorithmIdentifier algId;

    public static DigestInfo getInstance(ASN1TaggedObject obj, boolean explicit) {
        return DigestInfo.getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static DigestInfo getInstance(Object obj) {
        if (obj instanceof DigestInfo) {
            return (DigestInfo)obj;
        }
        if (obj != null) {
            return new DigestInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public DigestInfo(AlgorithmIdentifier algId, byte[] digest) {
        this.digest = Arrays.clone(digest);
        this.algId = algId;
    }

    public DigestInfo(ASN1Sequence obj) {
        Enumeration e = obj.getObjects();
        this.algId = AlgorithmIdentifier.getInstance(e.nextElement());
        this.digest = ASN1OctetString.getInstance(e.nextElement()).getOctets();
    }

    public AlgorithmIdentifier getAlgorithmId() {
        return this.algId;
    }

    public byte[] getDigest() {
        return Arrays.clone(this.digest);
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add(this.algId);
        v.add(new DEROctetString(this.digest));
        return new DERSequence(v);
    }
}

