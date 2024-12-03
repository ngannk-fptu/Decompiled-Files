/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.EncryptedDataEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

public class PKRecipientInfo
extends ASN1Object {
    private final HashedId8 recipientId;
    private final EncryptedDataEncryptionKey encKey;

    public PKRecipientInfo(HashedId8 recipientId, EncryptedDataEncryptionKey encKey) {
        this.recipientId = recipientId;
        this.encKey = encKey;
    }

    private PKRecipientInfo(ASN1Sequence sequence) {
        if (sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.recipientId = HashedId8.getInstance(sequence.getObjectAt(0));
        this.encKey = EncryptedDataEncryptionKey.getInstance(sequence.getObjectAt(1));
    }

    public static PKRecipientInfo getInstance(Object object) {
        if (object instanceof PKRecipientInfo) {
            return (PKRecipientInfo)((Object)object);
        }
        if (object != null) {
            return new PKRecipientInfo(ASN1Sequence.getInstance((Object)object));
        }
        return null;
    }

    public HashedId getRecipientId() {
        return this.recipientId;
    }

    public EncryptedDataEncryptionKey getEncKey() {
        return this.encKey;
    }

    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(new ASN1Encodable[]{this.recipientId, this.encKey});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private HashedId8 recipientId;
        private EncryptedDataEncryptionKey encKey;

        public Builder setRecipientId(HashedId8 recipientId) {
            this.recipientId = recipientId;
            return this;
        }

        public Builder setEncKey(EncryptedDataEncryptionKey encKey) {
            this.encKey = encKey;
            return this;
        }

        public PKRecipientInfo createPKRecipientInfo() {
            return new PKRecipientInfo(this.recipientId, this.encKey);
        }
    }
}

