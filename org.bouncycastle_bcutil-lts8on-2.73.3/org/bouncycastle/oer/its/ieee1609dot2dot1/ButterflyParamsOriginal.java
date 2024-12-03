/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.oer.its.ieee1609dot2dot1;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2dot1.ButterflyExpansion;

public class ButterflyParamsOriginal
extends ASN1Object {
    private final ButterflyExpansion signingExpansion;
    private final PublicEncryptionKey encryptionKey;
    private final ButterflyExpansion encryptionExpansion;

    public ButterflyParamsOriginal(ButterflyExpansion signingExpansion, PublicEncryptionKey encryptionKey, ButterflyExpansion encryptionExpansion) {
        this.signingExpansion = signingExpansion;
        this.encryptionKey = encryptionKey;
        this.encryptionExpansion = encryptionExpansion;
    }

    private ButterflyParamsOriginal(ASN1Sequence sequence) {
        if (sequence.size() != 3) {
            throw new IllegalArgumentException("expected sequence size of 3");
        }
        this.signingExpansion = ButterflyExpansion.getInstance(sequence.getObjectAt(0));
        this.encryptionKey = PublicEncryptionKey.getInstance(sequence.getObjectAt(1));
        this.encryptionExpansion = ButterflyExpansion.getInstance(sequence.getObjectAt(2));
    }

    public static ButterflyParamsOriginal getInstance(Object o) {
        if (o instanceof ButterflyParamsOriginal) {
            return (ButterflyParamsOriginal)((Object)o);
        }
        if (o != null) {
            return new ButterflyParamsOriginal(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.signingExpansion, this.encryptionKey, this.encryptionExpansion});
    }

    public ButterflyExpansion getSigningExpansion() {
        return this.signingExpansion;
    }

    public PublicEncryptionKey getEncryptionKey() {
        return this.encryptionKey;
    }

    public ButterflyExpansion getEncryptionExpansion() {
        return this.encryptionExpansion;
    }

    public static class Builder {
        private ButterflyExpansion signingExpansion;
        private PublicEncryptionKey encryptionKey;
        private ButterflyExpansion encryptionExpansion;

        public Builder setSigningExpansion(ButterflyExpansion signingExpansion) {
            this.signingExpansion = signingExpansion;
            return this;
        }

        public Builder setEncryptionKey(PublicEncryptionKey encryptionKey) {
            this.encryptionKey = encryptionKey;
            return this;
        }

        public Builder setEncryptionExpansion(ButterflyExpansion encryptionExpansion) {
            this.encryptionExpansion = encryptionExpansion;
            return this;
        }

        public ButterflyParamsOriginal createButterflyParamsOriginal() {
            return new ButterflyParamsOriginal(this.signingExpansion, this.encryptionKey, this.encryptionExpansion);
        }
    }
}

