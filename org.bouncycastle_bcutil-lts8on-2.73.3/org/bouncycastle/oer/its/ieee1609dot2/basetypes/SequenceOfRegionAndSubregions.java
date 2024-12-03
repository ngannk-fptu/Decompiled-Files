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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.RegionAndSubregions;

public class SequenceOfRegionAndSubregions
extends ASN1Object {
    private final List<RegionAndSubregions> regionAndSubregions;

    public SequenceOfRegionAndSubregions(List<RegionAndSubregions> items) {
        this.regionAndSubregions = Collections.unmodifiableList(items);
    }

    private SequenceOfRegionAndSubregions(ASN1Sequence s) {
        ArrayList<RegionAndSubregions> items = new ArrayList<RegionAndSubregions>();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            items.add(RegionAndSubregions.getInstance(it.next()));
        }
        this.regionAndSubregions = Collections.unmodifiableList(items);
    }

    public static SequenceOfRegionAndSubregions getInstance(Object o) {
        if (o instanceof SequenceOfRegionAndSubregions) {
            return (SequenceOfRegionAndSubregions)((Object)o);
        }
        if (o != null) {
            return new SequenceOfRegionAndSubregions(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public List<RegionAndSubregions> getRegionAndSubregions() {
        return this.regionAndSubregions;
    }

    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.regionAndSubregions);
    }
}

