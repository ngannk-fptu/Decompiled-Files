/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.mozilla;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.mozilla.PublicKeyAndChallenge;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class SignedPublicKeyAndChallenge
extends ASN1Object {
    private final PublicKeyAndChallenge pubKeyAndChal;
    private final ASN1Sequence pkacSeq;

    public static SignedPublicKeyAndChallenge getInstance(Object obj) {
        if (obj instanceof SignedPublicKeyAndChallenge) {
            return (SignedPublicKeyAndChallenge)obj;
        }
        if (obj != null) {
            return new SignedPublicKeyAndChallenge(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private SignedPublicKeyAndChallenge(ASN1Sequence seq) {
        this.pkacSeq = seq;
        this.pubKeyAndChal = PublicKeyAndChallenge.getInstance(seq.getObjectAt(0));
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.pkacSeq;
    }

    public PublicKeyAndChallenge getPublicKeyAndChallenge() {
        return this.pubKeyAndChal;
    }

    public AlgorithmIdentifier getSignatureAlgorithm() {
        return AlgorithmIdentifier.getInstance(this.pkacSeq.getObjectAt(1));
    }

    public ASN1BitString getSignature() {
        return ASN1BitString.getInstance(this.pkacSeq.getObjectAt(2));
    }
}

