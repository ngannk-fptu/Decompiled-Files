/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.BasePublicEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SymmAlgorithm;

public class PublicEncryptionKey
extends ASN1Object {
    private final SymmAlgorithm supportedSymmAlg;
    private final BasePublicEncryptionKey publicKey;

    public PublicEncryptionKey(SymmAlgorithm supportedSymmAlg, BasePublicEncryptionKey basePublicEncryptionKey) {
        this.supportedSymmAlg = supportedSymmAlg;
        this.publicKey = basePublicEncryptionKey;
    }

    private PublicEncryptionKey(ASN1Sequence seq) {
        if (seq.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.supportedSymmAlg = SymmAlgorithm.getInstance(seq.getObjectAt(0));
        this.publicKey = BasePublicEncryptionKey.getInstance(seq.getObjectAt(1));
    }

    public static PublicEncryptionKey getInstance(Object o) {
        if (o instanceof PublicEncryptionKey) {
            return (PublicEncryptionKey)((Object)o);
        }
        if (o != null) {
            return new PublicEncryptionKey(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public SymmAlgorithm getSupportedSymmAlg() {
        return this.supportedSymmAlg;
    }

    public BasePublicEncryptionKey getPublicKey() {
        return this.publicKey;
    }

    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(new ASN1Encodable[]{this.supportedSymmAlg, this.publicKey});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private SymmAlgorithm supportedSymmAlg;
        private BasePublicEncryptionKey publicKey;

        public Builder setSupportedSymmAlg(SymmAlgorithm supportedSymmAlg) {
            this.supportedSymmAlg = supportedSymmAlg;
            return this;
        }

        public Builder setPublicKey(BasePublicEncryptionKey basePublicEncryptionKey) {
            this.publicKey = basePublicEncryptionKey;
            return this;
        }

        public PublicEncryptionKey createPublicEncryptionKey() {
            return new PublicEncryptionKey(this.supportedSymmAlg, this.publicKey);
        }
    }
}

