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
package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.ieee1609dot2.HeaderInfo;
import org.bouncycastle.oer.its.ieee1609dot2.SignedDataPayload;

public class ToBeSignedData
extends ASN1Object {
    private final SignedDataPayload payload;
    private final HeaderInfo headerInfo;

    public ToBeSignedData(SignedDataPayload payload, HeaderInfo headerInfo) {
        this.payload = payload;
        this.headerInfo = headerInfo;
    }

    private ToBeSignedData(ASN1Sequence sequence) {
        if (sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.payload = SignedDataPayload.getInstance(sequence.getObjectAt(0));
        this.headerInfo = HeaderInfo.getInstance(sequence.getObjectAt(1));
    }

    public static ToBeSignedData getInstance(Object o) {
        if (o instanceof ToBeSignedData) {
            return (ToBeSignedData)((Object)o);
        }
        if (o != null) {
            return new ToBeSignedData(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public SignedDataPayload getPayload() {
        return this.payload;
    }

    public HeaderInfo getHeaderInfo() {
        return this.headerInfo;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.payload, this.headerInfo});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private SignedDataPayload payload;
        private HeaderInfo headerInfo;

        public Builder setPayload(SignedDataPayload payload) {
            this.payload = payload;
            return this;
        }

        public Builder setHeaderInfo(HeaderInfo headerInfo) {
            this.headerInfo = headerInfo;
            return this;
        }

        public ToBeSignedData createToBeSignedData() {
            return new ToBeSignedData(this.payload, this.headerInfo);
        }
    }
}

