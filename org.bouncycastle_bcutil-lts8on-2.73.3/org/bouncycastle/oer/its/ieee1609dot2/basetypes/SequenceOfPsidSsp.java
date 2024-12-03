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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PsidSsp;

public class SequenceOfPsidSsp
extends ASN1Object {
    private final List<PsidSsp> psidSsps;

    public SequenceOfPsidSsp(List<PsidSsp> items) {
        this.psidSsps = Collections.unmodifiableList(items);
    }

    private SequenceOfPsidSsp(ASN1Sequence sequence) {
        ArrayList<PsidSsp> accumulator = new ArrayList<PsidSsp>();
        Iterator it = sequence.iterator();
        while (it.hasNext()) {
            accumulator.add(PsidSsp.getInstance(it.next()));
        }
        this.psidSsps = Collections.unmodifiableList(accumulator);
    }

    public static SequenceOfPsidSsp getInstance(Object o) {
        if (o instanceof SequenceOfPsidSsp) {
            return (SequenceOfPsidSsp)((Object)o);
        }
        if (o != null) {
            return new SequenceOfPsidSsp(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<PsidSsp> getPsidSsps() {
        return this.psidSsps;
    }

    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.psidSsps);
    }

    public static class Builder {
        private List<PsidSsp> items = new ArrayList<PsidSsp>();

        public Builder setItems(List<PsidSsp> items) {
            this.items = items;
            return this;
        }

        public Builder setItem(PsidSsp ... items) {
            for (int i = 0; i != items.length; ++i) {
                PsidSsp item = items[i];
                this.items.add(item);
            }
            return this;
        }

        public SequenceOfPsidSsp createSequenceOfPsidSsp() {
            return new SequenceOfPsidSsp(this.items);
        }
    }
}

