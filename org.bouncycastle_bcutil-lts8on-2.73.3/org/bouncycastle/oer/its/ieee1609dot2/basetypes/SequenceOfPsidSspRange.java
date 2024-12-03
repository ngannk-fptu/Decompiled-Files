/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PsidSspRange;

public class SequenceOfPsidSspRange
extends ASN1Object {
    private final List<PsidSspRange> psidSspRanges;

    public SequenceOfPsidSspRange(List<PsidSspRange> items) {
        this.psidSspRanges = Collections.unmodifiableList(items);
    }

    private SequenceOfPsidSspRange(ASN1Sequence sequence) {
        ArrayList<PsidSspRange> l = new ArrayList<PsidSspRange>();
        Iterator e = sequence.iterator();
        while (e.hasNext()) {
            l.add(PsidSspRange.getInstance(e.next()));
        }
        this.psidSspRanges = Collections.unmodifiableList(l);
    }

    public static SequenceOfPsidSspRange getInstance(Object o) {
        if (o instanceof SequenceOfPsidSspRange) {
            return (SequenceOfPsidSspRange)((Object)o);
        }
        if (o != null) {
            return new SequenceOfPsidSspRange(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public List<PsidSspRange> getPsidSspRanges() {
        return this.psidSspRanges;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector avec = new ASN1EncodableVector();
        Iterator<PsidSspRange> it = this.psidSspRanges.iterator();
        while (it.hasNext()) {
            avec.add((ASN1Encodable)it.next());
        }
        return new DERSequence(avec);
    }

    public static class Builder {
        private final ArrayList<PsidSspRange> psidSspRanges = new ArrayList();

        public Builder add(PsidSspRange ... ranges) {
            this.psidSspRanges.addAll(Arrays.asList(ranges));
            return this;
        }

        public SequenceOfPsidSspRange build() {
            return new SequenceOfPsidSspRange(this.psidSspRanges);
        }
    }
}

