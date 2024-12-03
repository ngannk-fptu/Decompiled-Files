/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class DHBMParameter
extends ASN1Object {
    private final AlgorithmIdentifier owf;
    private final AlgorithmIdentifier mac;

    private DHBMParameter(ASN1Sequence sequence) {
        if (sequence.size() != 2) {
            throw new IllegalArgumentException("expecting sequence size of 2");
        }
        this.owf = AlgorithmIdentifier.getInstance((Object)sequence.getObjectAt(0));
        this.mac = AlgorithmIdentifier.getInstance((Object)sequence.getObjectAt(1));
    }

    public DHBMParameter(AlgorithmIdentifier owf, AlgorithmIdentifier mac) {
        this.owf = owf;
        this.mac = mac;
    }

    public static DHBMParameter getInstance(Object o) {
        if (o instanceof DHBMParameter) {
            return (DHBMParameter)((Object)o);
        }
        if (o != null) {
            return new DHBMParameter(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public AlgorithmIdentifier getOwf() {
        return this.owf;
    }

    public AlgorithmIdentifier getMac() {
        return this.mac;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.owf, this.mac});
    }
}

