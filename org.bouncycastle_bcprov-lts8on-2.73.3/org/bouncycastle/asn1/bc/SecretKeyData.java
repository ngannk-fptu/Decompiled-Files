/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class SecretKeyData
extends ASN1Object {
    private final ASN1ObjectIdentifier keyAlgorithm;
    private final ASN1OctetString keyBytes;

    public SecretKeyData(ASN1ObjectIdentifier keyAlgorithm, byte[] keyBytes) {
        this.keyAlgorithm = keyAlgorithm;
        this.keyBytes = new DEROctetString(Arrays.clone(keyBytes));
    }

    private SecretKeyData(ASN1Sequence seq) {
        this.keyAlgorithm = ASN1ObjectIdentifier.getInstance(seq.getObjectAt(0));
        this.keyBytes = ASN1OctetString.getInstance(seq.getObjectAt(1));
    }

    public static SecretKeyData getInstance(Object o) {
        if (o instanceof SecretKeyData) {
            return (SecretKeyData)o;
        }
        if (o != null) {
            return new SecretKeyData(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public byte[] getKeyBytes() {
        return Arrays.clone(this.keyBytes.getOctets());
    }

    public ASN1ObjectIdentifier getKeyAlgorithm() {
        return this.keyAlgorithm;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add(this.keyAlgorithm);
        v.add(this.keyBytes);
        return new DERSequence(v);
    }
}

