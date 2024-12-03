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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Latitude;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Longitude;

public class TwoDLocation
extends ASN1Object {
    private final Latitude latitude;
    private final Longitude longitude;

    public TwoDLocation(Latitude latitude, Longitude longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private TwoDLocation(ASN1Sequence seq) {
        if (seq.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.latitude = Latitude.getInstance(seq.getObjectAt(0));
        this.longitude = Longitude.getInstance(seq.getObjectAt(1));
    }

    public static TwoDLocation getInstance(Object o) {
        if (o instanceof TwoDLocation) {
            return (TwoDLocation)((Object)o);
        }
        if (o != null) {
            return new TwoDLocation(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.latitude, this.longitude});
    }

    public Latitude getLatitude() {
        return this.latitude;
    }

    public Longitude getLongitude() {
        return this.longitude;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Latitude latitude;
        private Longitude longitude;

        public Builder setLatitude(Latitude latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder setLongitude(Longitude longitude) {
            this.longitude = longitude;
            return this;
        }

        public TwoDLocation createTwoDLocation() {
            return new TwoDLocation(this.latitude, this.longitude);
        }
    }
}

