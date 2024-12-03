/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.CountryAndRegions;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.CountryAndSubregions;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.CountryOnly;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.RegionInterface;

public class IdentifiedRegion
extends ASN1Object
implements ASN1Choice,
RegionInterface {
    public static final int countryOnly = 0;
    public static final int countryAndRegions = 1;
    public static final int countryAndSubregions = 2;
    private final int choice;
    private final ASN1Encodable identifiedRegion;

    public IdentifiedRegion(int choice, ASN1Encodable region) {
        this.choice = choice;
        this.identifiedRegion = region;
    }

    private IdentifiedRegion(ASN1TaggedObject ato) {
        this.choice = ato.getTagNo();
        switch (this.choice) {
            case 0: {
                this.identifiedRegion = CountryOnly.getInstance(ato.getExplicitBaseObject());
                break;
            }
            case 1: {
                this.identifiedRegion = CountryAndRegions.getInstance(ato.getExplicitBaseObject());
                break;
            }
            case 2: {
                this.identifiedRegion = CountryAndSubregions.getInstance(ato.getExplicitBaseObject());
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + this.choice);
            }
        }
    }

    public static IdentifiedRegion countryOnly(CountryOnly only) {
        return new IdentifiedRegion(0, (ASN1Encodable)only);
    }

    public static IdentifiedRegion countryAndRegions(CountryAndRegions value) {
        return new IdentifiedRegion(1, (ASN1Encodable)value);
    }

    public static IdentifiedRegion countryAndSubregions(CountryAndSubregions countryAndSubregions) {
        return new IdentifiedRegion(2, (ASN1Encodable)countryAndSubregions);
    }

    public static IdentifiedRegion getInstance(Object o) {
        if (o instanceof IdentifiedRegion) {
            return (IdentifiedRegion)o;
        }
        if (o != null) {
            return new IdentifiedRegion(ASN1TaggedObject.getInstance((Object)o, (int)128));
        }
        return null;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getIdentifiedRegion() {
        return this.identifiedRegion;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.identifiedRegion);
    }
}

