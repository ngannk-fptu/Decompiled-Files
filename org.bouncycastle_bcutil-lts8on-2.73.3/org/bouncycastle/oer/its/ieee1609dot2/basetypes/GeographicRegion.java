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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.CircularRegion;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PolygonalRegion;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfIdentifiedRegion;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfRectangularRegion;

public class GeographicRegion
extends ASN1Object
implements ASN1Choice {
    public static final int circularRegion = 0;
    public static final int rectangularRegion = 1;
    public static final int polygonalRegion = 2;
    public static final int identifiedRegion = 3;
    private final int choice;
    private final ASN1Encodable geographicRegion;

    public GeographicRegion(int choice, ASN1Encodable region) {
        this.choice = choice;
        this.geographicRegion = region;
    }

    private GeographicRegion(ASN1TaggedObject taggedObject) {
        this.choice = taggedObject.getTagNo();
        switch (this.choice) {
            case 0: {
                this.geographicRegion = CircularRegion.getInstance(taggedObject.getExplicitBaseObject());
                break;
            }
            case 1: {
                this.geographicRegion = SequenceOfRectangularRegion.getInstance(taggedObject.getExplicitBaseObject());
                break;
            }
            case 2: {
                this.geographicRegion = PolygonalRegion.getInstance(taggedObject.getExplicitBaseObject());
                break;
            }
            case 3: {
                this.geographicRegion = SequenceOfIdentifiedRegion.getInstance(taggedObject.getExplicitBaseObject());
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + this.choice);
            }
        }
    }

    public static GeographicRegion circularRegion(CircularRegion region) {
        return new GeographicRegion(0, (ASN1Encodable)region);
    }

    public static GeographicRegion rectangularRegion(SequenceOfRectangularRegion region) {
        return new GeographicRegion(1, (ASN1Encodable)region);
    }

    public static GeographicRegion polygonalRegion(PolygonalRegion region) {
        return new GeographicRegion(2, (ASN1Encodable)region);
    }

    public static GeographicRegion identifiedRegion(SequenceOfIdentifiedRegion region) {
        return new GeographicRegion(3, (ASN1Encodable)region);
    }

    public static GeographicRegion getInstance(Object o) {
        if (o instanceof GeographicRegion) {
            return (GeographicRegion)((Object)o);
        }
        if (o != null) {
            return new GeographicRegion(ASN1TaggedObject.getInstance((Object)o, (int)128));
        }
        return null;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getGeographicRegion() {
        return this.geographicRegion;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.geographicRegion);
    }
}

