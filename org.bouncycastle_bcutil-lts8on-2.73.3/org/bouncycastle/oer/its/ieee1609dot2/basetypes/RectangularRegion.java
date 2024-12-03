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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.RegionInterface;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.TwoDLocation;

public class RectangularRegion
extends ASN1Object
implements RegionInterface {
    private final TwoDLocation northWest;
    private final TwoDLocation southEast;

    public RectangularRegion(TwoDLocation northWest, TwoDLocation southEast) {
        this.northWest = northWest;
        this.southEast = southEast;
    }

    private RectangularRegion(ASN1Sequence s) {
        if (s.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.northWest = TwoDLocation.getInstance(s.getObjectAt(0));
        this.southEast = TwoDLocation.getInstance(s.getObjectAt(1));
    }

    public static RectangularRegion getInstance(Object o) {
        if (o instanceof RectangularRegion) {
            return (RectangularRegion)o;
        }
        if (o != null) {
            return new RectangularRegion(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public TwoDLocation getNorthWest() {
        return this.northWest;
    }

    public TwoDLocation getSouthEast() {
        return this.southEast;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.northWest, this.southEast});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TwoDLocation northWest;
        private TwoDLocation southEast;

        public Builder setNorthWest(TwoDLocation northWest) {
            this.northWest = northWest;
            return this;
        }

        public Builder setSouthEast(TwoDLocation southEast) {
            this.southEast = southEast;
            return this;
        }

        public RectangularRegion createRectangularRegion() {
            return new RectangularRegion(this.northWest, this.southEast);
        }
    }
}

