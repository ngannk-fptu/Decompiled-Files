/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x9;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class KeySpecificInfo
extends ASN1Object {
    private ASN1ObjectIdentifier algorithm;
    private ASN1OctetString counter;

    public KeySpecificInfo(ASN1ObjectIdentifier algorithm, ASN1OctetString counter) {
        this.algorithm = algorithm;
        this.counter = counter;
    }

    public static KeySpecificInfo getInstance(Object obj) {
        if (obj instanceof KeySpecificInfo) {
            return (KeySpecificInfo)obj;
        }
        if (obj != null) {
            return new KeySpecificInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private KeySpecificInfo(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        this.algorithm = (ASN1ObjectIdentifier)e.nextElement();
        this.counter = (ASN1OctetString)e.nextElement();
    }

    public ASN1ObjectIdentifier getAlgorithm() {
        return this.algorithm;
    }

    public ASN1OctetString getCounter() {
        return this.counter;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add(this.algorithm);
        v.add(this.counter);
        return new DERSequence(v);
    }
}

