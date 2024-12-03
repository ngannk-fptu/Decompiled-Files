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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.RectangularRegion;

public class SequenceOfRectangularRegion
extends ASN1Object {
    private final List<RectangularRegion> rectangularRegions;

    public SequenceOfRectangularRegion(List<RectangularRegion> items) {
        this.rectangularRegions = Collections.unmodifiableList(items);
    }

    private SequenceOfRectangularRegion(ASN1Sequence s) {
        ArrayList<RectangularRegion> l = new ArrayList<RectangularRegion>();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            l.add(RectangularRegion.getInstance(it.next()));
        }
        this.rectangularRegions = Collections.unmodifiableList(l);
    }

    public static SequenceOfRectangularRegion getInstance(Object o) {
        if (o instanceof SequenceOfRectangularRegion) {
            return (SequenceOfRectangularRegion)((Object)o);
        }
        if (o != null) {
            return new SequenceOfRectangularRegion(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public List<RectangularRegion> getRectangularRegions() {
        return this.rectangularRegions;
    }

    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.rectangularRegions);
    }
}

