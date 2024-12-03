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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Psid;

public class SequenceOfPsid
extends ASN1Object {
    private final List<Psid> psids;

    public SequenceOfPsid(List<Psid> items) {
        this.psids = Collections.unmodifiableList(items);
    }

    private SequenceOfPsid(ASN1Sequence sequence) {
        ArrayList<Psid> accumulator = new ArrayList<Psid>();
        Iterator it = sequence.iterator();
        while (it.hasNext()) {
            accumulator.add(Psid.getInstance(it.next()));
        }
        this.psids = Collections.unmodifiableList(accumulator);
    }

    public static SequenceOfPsid getInstance(Object o) {
        if (o instanceof SequenceOfPsid) {
            return (SequenceOfPsid)((Object)o);
        }
        if (o != null) {
            return new SequenceOfPsid(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<Psid> getPsids() {
        return this.psids;
    }

    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.psids);
    }

    public static class Builder {
        private List<Psid> items = new ArrayList<Psid>();

        public Builder setItems(List<Psid> items) {
            this.items = items;
            return this;
        }

        public Builder setItem(Psid ... items) {
            for (int i = 0; i != items.length; ++i) {
                Psid item = items[i];
                this.items.add(item);
            }
            return this;
        }

        public SequenceOfPsid createSequenceOfPsidSsp() {
            return new SequenceOfPsid(this.items);
        }
    }
}

