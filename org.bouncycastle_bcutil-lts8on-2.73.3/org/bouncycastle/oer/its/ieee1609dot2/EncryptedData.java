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
import org.bouncycastle.oer.its.ieee1609dot2.SequenceOfRecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.SymmetricCiphertext;

public class EncryptedData
extends ASN1Object {
    private final SequenceOfRecipientInfo recipients;
    private final SymmetricCiphertext ciphertext;

    public EncryptedData(SequenceOfRecipientInfo recipients, SymmetricCiphertext ciphertext) {
        this.recipients = recipients;
        this.ciphertext = ciphertext;
    }

    private EncryptedData(ASN1Sequence sequence) {
        if (sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.recipients = SequenceOfRecipientInfo.getInstance(sequence.getObjectAt(0));
        this.ciphertext = SymmetricCiphertext.getInstance(sequence.getObjectAt(1));
    }

    public static EncryptedData getInstance(Object o) {
        if (o instanceof EncryptedData) {
            return (EncryptedData)((Object)o);
        }
        if (o != null) {
            return new EncryptedData(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(new ASN1Encodable[]{this.recipients, this.ciphertext});
    }

    public SequenceOfRecipientInfo getRecipients() {
        return this.recipients;
    }

    public SymmetricCiphertext getCiphertext() {
        return this.ciphertext;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private SequenceOfRecipientInfo recipients;
        private SymmetricCiphertext ciphertext;

        public Builder setRecipients(SequenceOfRecipientInfo recipients) {
            this.recipients = recipients;
            return this;
        }

        public Builder setCiphertext(SymmetricCiphertext ciphertext) {
            this.ciphertext = ciphertext;
            return this;
        }

        public EncryptedData createEncryptedData() {
            return new EncryptedData(this.recipients, this.ciphertext);
        }
    }
}

