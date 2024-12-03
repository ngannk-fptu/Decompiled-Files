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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class SequenceOfUint8
extends ASN1Object {
    private final List<UINT8> uint8s;

    public SequenceOfUint8(List<UINT8> values) {
        this.uint8s = Collections.unmodifiableList(values);
    }

    private SequenceOfUint8(ASN1Sequence sequence) {
        ArrayList<UINT8> items = new ArrayList<UINT8>();
        Iterator it = sequence.iterator();
        while (it.hasNext()) {
            items.add(UINT8.getInstance(it.next()));
        }
        this.uint8s = Collections.unmodifiableList(items);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SequenceOfUint8 getInstance(Object o) {
        if (o instanceof SequenceOfUint8) {
            return (SequenceOfUint8)((Object)o);
        }
        if (o != null) {
            return new SequenceOfUint8(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public List<UINT8> getUint8s() {
        return this.uint8s;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector vector = new ASN1EncodableVector();
        for (UINT8 uint8 : this.uint8s) {
            vector.add((ASN1Encodable)uint8.toASN1Primitive());
        }
        return new DERSequence(vector);
    }

    public static class Builder {
        private final List<UINT8> items = new ArrayList<UINT8>();

        public Builder addHashId3(UINT8 ... items) {
            this.items.addAll(Arrays.asList(items));
            return this;
        }

        public SequenceOfUint8 build() {
            return new SequenceOfUint8(this.items);
        }
    }
}

