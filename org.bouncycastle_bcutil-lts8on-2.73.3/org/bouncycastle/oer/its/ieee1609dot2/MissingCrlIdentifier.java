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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.CrlSeries;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId3;

public class MissingCrlIdentifier
extends ASN1Object {
    private final HashedId3 cracaId;
    private final CrlSeries crlSeries;

    public MissingCrlIdentifier(HashedId3 cracaId, CrlSeries crlSeries) {
        this.cracaId = cracaId;
        this.crlSeries = crlSeries;
    }

    private MissingCrlIdentifier(ASN1Sequence sequence) {
        if (sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.cracaId = HashedId3.getInstance(sequence.getObjectAt(0));
        this.crlSeries = CrlSeries.getInstance(sequence.getObjectAt(1));
    }

    public static MissingCrlIdentifier getInstance(Object src) {
        if (src instanceof MissingCrlIdentifier) {
            return (MissingCrlIdentifier)((Object)src);
        }
        if (src != null) {
            return new MissingCrlIdentifier(ASN1Sequence.getInstance((Object)src));
        }
        return null;
    }

    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(new ASN1Encodable[]{this.cracaId, this.crlSeries});
    }

    public HashedId3 getCracaId() {
        return this.cracaId;
    }

    public CrlSeries getCrlSeries() {
        return this.crlSeries;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private HashedId3 cracaId;
        private CrlSeries crlSeries;

        public Builder setCracaId(HashedId3 cracaId) {
            this.cracaId = cracaId;
            return this;
        }

        public Builder setCrlSeries(CrlSeries crlSeries) {
            this.crlSeries = crlSeries;
            return this;
        }

        public MissingCrlIdentifier createMissingCrlIdentifier() {
            return new MissingCrlIdentifier(this.cracaId, this.crlSeries);
        }
    }
}

