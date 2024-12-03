/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.RegionInterface;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.TwoDLocation;

public class PolygonalRegion
extends ASN1Object
implements RegionInterface {
    private final List<TwoDLocation> twoDLocations;

    public PolygonalRegion(List<TwoDLocation> locations) {
        this.twoDLocations = Collections.unmodifiableList(locations);
    }

    private PolygonalRegion(ASN1Sequence s) {
        ArrayList<TwoDLocation> l = new ArrayList<TwoDLocation>();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            l.add(TwoDLocation.getInstance(it.next()));
        }
        this.twoDLocations = Collections.unmodifiableList(l);
    }

    public static PolygonalRegion getInstance(Object o) {
        if (o instanceof PolygonalRegion) {
            return (PolygonalRegion)o;
        }
        if (o != null) {
            return new PolygonalRegion(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public List<TwoDLocation> getTwoDLocations() {
        return this.twoDLocations;
    }

    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.twoDLocations);
    }

    public static class Builder {
        private List<TwoDLocation> locations = new ArrayList<TwoDLocation>();

        public Builder setLocations(List<TwoDLocation> locations) {
            this.locations = locations;
            return this;
        }

        public Builder setLocations(TwoDLocation ... locations) {
            this.locations.addAll(Arrays.asList(locations));
            return this;
        }

        public PolygonalRegion createPolygonalRegion() {
            return new PolygonalRegion(this.locations);
        }
    }
}

