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
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Psid;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SspRange;

public class PsidSspRange
extends ASN1Object {
    private final Psid psid;
    private final SspRange sspRange;

    public PsidSspRange(Psid psid, SspRange sspRange) {
        this.psid = psid;
        this.sspRange = sspRange;
    }

    private PsidSspRange(ASN1Sequence sequence) {
        if (sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.psid = Psid.getInstance(sequence.getObjectAt(0));
        this.sspRange = OEROptional.getValue(SspRange.class, sequence.getObjectAt(1));
    }

    public static PsidSspRange getInstance(Object src) {
        if (src instanceof PsidSspRange) {
            return (PsidSspRange)((Object)src);
        }
        if (src != null) {
            return new PsidSspRange(ASN1Sequence.getInstance((Object)src));
        }
        return null;
    }

    public Psid getPsid() {
        return this.psid;
    }

    public SspRange getSspRange() {
        return this.sspRange;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.psid, OEROptional.getInstance((Object)this.sspRange)});
    }

    public static class Builder {
        private Psid psid;
        private SspRange sspRange;

        public Builder setPsid(Psid psid) {
            this.psid = psid;
            return this;
        }

        public Builder setPsid(long psid) {
            this.psid = new Psid(psid);
            return this;
        }

        public Builder setSspRange(SspRange sspRange) {
            this.sspRange = sspRange;
            return this;
        }

        public PsidSspRange createPsidSspRange() {
            return new PsidSspRange(this.psid, this.sspRange);
        }
    }
}

