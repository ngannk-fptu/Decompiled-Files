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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId3;

public class SequenceOfHashedId3
extends ASN1Object {
    private final List<HashedId3> hashedId3s;

    public SequenceOfHashedId3(List<HashedId3> hashedId3s) {
        this.hashedId3s = Collections.unmodifiableList(hashedId3s);
    }

    private SequenceOfHashedId3(ASN1Sequence sequence) {
        ArrayList<HashedId3> items = new ArrayList<HashedId3>();
        Iterator it = sequence.iterator();
        while (it.hasNext()) {
            items.add(HashedId3.getInstance(it.next()));
        }
        this.hashedId3s = Collections.unmodifiableList(items);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SequenceOfHashedId3 getInstance(Object o) {
        if (o instanceof SequenceOfHashedId3) {
            return (SequenceOfHashedId3)((Object)o);
        }
        if (o != null) {
            return new SequenceOfHashedId3(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public List<HashedId3> getHashedId3s() {
        return this.hashedId3s;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.hashedId3s.toArray(new ASN1Encodable[0]));
    }

    public static class Builder {
        private final List<HashedId3> items = new ArrayList<HashedId3>();

        public Builder addHashId3(HashedId3 ... items) {
            this.items.addAll(Arrays.asList(items));
            return this;
        }

        public SequenceOfHashedId3 build() {
            return new SequenceOfHashedId3(this.items);
        }
    }
}

