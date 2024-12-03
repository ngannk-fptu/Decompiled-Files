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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

public class SequenceOfHashedId8
extends ASN1Object {
    private final List<HashedId8> hashedId8s;

    public SequenceOfHashedId8(List<HashedId8> hashedId8s) {
        this.hashedId8s = Collections.unmodifiableList(hashedId8s);
    }

    private SequenceOfHashedId8(ASN1Sequence sequence) {
        ArrayList<HashedId8> items = new ArrayList<HashedId8>();
        Iterator it = sequence.iterator();
        while (it.hasNext()) {
            items.add(HashedId8.getInstance(it.next()));
        }
        this.hashedId8s = Collections.unmodifiableList(items);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SequenceOfHashedId8 getInstance(Object o) {
        if (o instanceof SequenceOfHashedId8) {
            return (SequenceOfHashedId8)((Object)o);
        }
        if (o != null) {
            return new SequenceOfHashedId8(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public List<HashedId8> getHashedId8s() {
        return this.hashedId8s;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.hashedId8s.toArray(new ASN1Encodable[0]));
    }

    public static class Builder {
        private final List<HashedId8> items = new ArrayList<HashedId8>();

        public Builder addHashId8(HashedId8 ... items) {
            this.items.addAll(Arrays.asList(items));
            return this;
        }

        public SequenceOfHashedId8 build() {
            return new SequenceOfHashedId8(this.items);
        }
    }
}

