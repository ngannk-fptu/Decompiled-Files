/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.mozilla;

import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class PublicKeyAndChallenge
extends ASN1Object {
    private ASN1Sequence pkacSeq;
    private SubjectPublicKeyInfo spki;
    private ASN1IA5String challenge;

    public static PublicKeyAndChallenge getInstance(Object obj) {
        if (obj instanceof PublicKeyAndChallenge) {
            return (PublicKeyAndChallenge)obj;
        }
        if (obj != null) {
            return new PublicKeyAndChallenge(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private PublicKeyAndChallenge(ASN1Sequence seq) {
        this.pkacSeq = seq;
        this.spki = SubjectPublicKeyInfo.getInstance(seq.getObjectAt(0));
        this.challenge = ASN1IA5String.getInstance(seq.getObjectAt(1));
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.pkacSeq;
    }

    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.spki;
    }

    public ASN1IA5String getChallengeIA5() {
        return this.challenge;
    }
}

