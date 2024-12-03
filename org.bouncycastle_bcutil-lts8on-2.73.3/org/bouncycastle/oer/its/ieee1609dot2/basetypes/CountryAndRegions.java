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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.CountryOnly;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.RegionInterface;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfUint8;

public class CountryAndRegions
extends ASN1Object
implements RegionInterface {
    private final CountryOnly countryOnly;
    private final SequenceOfUint8 regions;

    public CountryAndRegions(CountryOnly countryOnly, SequenceOfUint8 regionList) {
        this.countryOnly = countryOnly;
        this.regions = SequenceOfUint8.getInstance((Object)regionList);
    }

    private CountryAndRegions(ASN1Sequence sequence) {
        if (sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.countryOnly = CountryOnly.getInstance(sequence.getObjectAt(0));
        this.regions = SequenceOfUint8.getInstance(sequence.getObjectAt(1));
    }

    public static CountryAndRegions getInstance(Object object) {
        if (object instanceof CountryAndRegions) {
            return (CountryAndRegions)object;
        }
        if (object != null) {
            return new CountryAndRegions(ASN1Sequence.getInstance((Object)object));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(new ASN1Encodable[]{this.countryOnly, this.regions});
    }

    public CountryOnly getCountryOnly() {
        return this.countryOnly;
    }

    public SequenceOfUint8 getRegions() {
        return this.regions;
    }

    public static class Builder {
        private SequenceOfUint8 regionList;
        private CountryOnly countryOnly;

        public Builder setCountryOnly(CountryOnly countryOnly) {
            this.countryOnly = countryOnly;
            return this;
        }

        public Builder setRegions(SequenceOfUint8 regionList) {
            this.regionList = regionList;
            return this;
        }

        public CountryAndRegions createCountryAndRegions() {
            return new CountryAndRegions(this.countryOnly, this.regionList);
        }
    }
}

