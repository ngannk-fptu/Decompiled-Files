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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Elevation;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Latitude;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Longitude;

public class ThreeDLocation
extends ASN1Object {
    private final Latitude latitude;
    private final Longitude longitude;
    private final Elevation elevation;

    public ThreeDLocation(Latitude latitude, Longitude longitude, Elevation elevation) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
    }

    private ThreeDLocation(ASN1Sequence sequence) {
        if (sequence.size() != 3) {
            throw new IllegalArgumentException("expected sequence size of 3");
        }
        this.latitude = Latitude.getInstance(sequence.getObjectAt(0));
        this.longitude = Longitude.getInstance(sequence.getObjectAt(1));
        this.elevation = Elevation.getInstance(sequence.getObjectAt(2));
    }

    public static ThreeDLocation getInstance(Object o) {
        if (o instanceof ThreeDLocation) {
            return (ThreeDLocation)((Object)o);
        }
        if (o != null) {
            return new ThreeDLocation(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.latitude, this.longitude, this.elevation});
    }

    public Latitude getLatitude() {
        return this.latitude;
    }

    public Longitude getLongitude() {
        return this.longitude;
    }

    public Elevation getElevation() {
        return this.elevation;
    }

    public static class Builder {
        private Latitude latitude;
        private Longitude longitude;
        private Elevation elevation;

        public Builder setLatitude(Latitude latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder setLongitude(Longitude longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder setElevation(Elevation elevation) {
            this.elevation = elevation;
            return this;
        }

        public ThreeDLocation createThreeDLocation() {
            return new ThreeDLocation(this.latitude, this.longitude, this.elevation);
        }
    }
}

