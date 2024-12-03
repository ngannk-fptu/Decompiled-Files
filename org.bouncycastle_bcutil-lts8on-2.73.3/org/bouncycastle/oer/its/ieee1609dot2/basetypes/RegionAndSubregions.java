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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.RegionInterface;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfUint16;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class RegionAndSubregions
extends ASN1Object
implements RegionInterface {
    private final UINT8 region;
    private final SequenceOfUint16 subregions;

    public RegionAndSubregions(UINT8 region, SequenceOfUint16 subRegions) {
        this.region = region;
        this.subregions = subRegions;
    }

    private RegionAndSubregions(ASN1Sequence seq) {
        if (seq.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.region = UINT8.getInstance(seq.getObjectAt(0));
        this.subregions = SequenceOfUint16.getInstance(seq.getObjectAt(1));
    }

    public UINT8 getRegion() {
        return this.region;
    }

    public SequenceOfUint16 getSubregions() {
        return this.subregions;
    }

    public static RegionAndSubregions getInstance(Object o) {
        if (o instanceof RegionAndSubregions) {
            return (RegionAndSubregions)o;
        }
        if (o != null) {
            return new RegionAndSubregions(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(new ASN1Encodable[]{this.region, this.subregions});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UINT8 region;
        private SequenceOfUint16 subRegions;

        public Builder setRegion(UINT8 region) {
            this.region = region;
            return this;
        }

        public Builder setSubregions(SequenceOfUint16 subRegions) {
            this.subRegions = subRegions;
            return this;
        }

        public RegionAndSubregions createRegionAndSubregions() {
            return new RegionAndSubregions(this.region, this.subRegions);
        }
    }
}

