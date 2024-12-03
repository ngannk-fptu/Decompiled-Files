/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;

public class ExternalValue
extends ASN1Object {
    private final GeneralName location;
    private final AlgorithmIdentifier hashAlg;
    private final ASN1BitString hashVal;

    public ExternalValue(GeneralName location, AlgorithmIdentifier hashAlg, byte[] hashVal) {
        this.location = location;
        this.hashAlg = hashAlg;
        this.hashVal = new DERBitString(hashVal);
    }

    private ExternalValue(ASN1Sequence seq) {
        if (seq.size() != 3) {
            throw new IllegalArgumentException("unknown sequence");
        }
        this.location = GeneralName.getInstance(seq.getObjectAt(0));
        this.hashAlg = AlgorithmIdentifier.getInstance(seq.getObjectAt(1));
        this.hashVal = ASN1BitString.getInstance(seq.getObjectAt(2));
    }

    public static ExternalValue getInstance(Object o) {
        if (o instanceof ExternalValue) {
            return (ExternalValue)o;
        }
        if (o != null) {
            return new ExternalValue(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public GeneralName getLocation() {
        return this.location;
    }

    public AlgorithmIdentifier getHashAlg() {
        return this.hashAlg;
    }

    public ASN1BitString getHashVal() {
        return this.hashVal;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.location);
        v.add(this.hashAlg);
        v.add(this.hashVal);
        return new DERSequence(v);
    }
}

