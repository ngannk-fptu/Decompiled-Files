/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cmp.Challenge;

public class POPODecKeyChallContent
extends ASN1Object {
    private final ASN1Sequence content;

    private POPODecKeyChallContent(ASN1Sequence seq) {
        this.content = seq;
    }

    public static POPODecKeyChallContent getInstance(Object o) {
        if (o instanceof POPODecKeyChallContent) {
            return (POPODecKeyChallContent)((Object)o);
        }
        if (o != null) {
            return new POPODecKeyChallContent(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public Challenge[] toChallengeArray() {
        Challenge[] result = new Challenge[this.content.size()];
        for (int i = 0; i != result.length; ++i) {
            result[i] = Challenge.getInstance(this.content.getObjectAt(i));
        }
        return result;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}

