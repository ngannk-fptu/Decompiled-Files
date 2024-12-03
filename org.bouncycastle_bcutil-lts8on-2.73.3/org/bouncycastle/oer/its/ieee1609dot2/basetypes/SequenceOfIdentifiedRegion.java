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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.IdentifiedRegion;

public class SequenceOfIdentifiedRegion
extends ASN1Object {
    private final List<IdentifiedRegion> identifiedRegions;

    public SequenceOfIdentifiedRegion(List<IdentifiedRegion> identifiedRegions) {
        this.identifiedRegions = Collections.unmodifiableList(identifiedRegions);
    }

    private SequenceOfIdentifiedRegion(ASN1Sequence s) {
        ArrayList<IdentifiedRegion> l = new ArrayList<IdentifiedRegion>();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            l.add(IdentifiedRegion.getInstance(it.next()));
        }
        this.identifiedRegions = Collections.unmodifiableList(l);
    }

    public static SequenceOfIdentifiedRegion getInstance(Object o) {
        if (o instanceof SequenceOfIdentifiedRegion) {
            return (SequenceOfIdentifiedRegion)((Object)o);
        }
        if (o != null) {
            return new SequenceOfIdentifiedRegion(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public List<IdentifiedRegion> getIdentifiedRegions() {
        return this.identifiedRegions;
    }

    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.identifiedRegions);
    }
}

