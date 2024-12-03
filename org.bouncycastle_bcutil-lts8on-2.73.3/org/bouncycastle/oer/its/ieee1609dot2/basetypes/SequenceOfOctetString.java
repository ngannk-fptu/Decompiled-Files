/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;

public class SequenceOfOctetString
extends ASN1Object {
    private final List<ASN1OctetString> octetStrings;

    public SequenceOfOctetString(List<ASN1OctetString> octetStrings) {
        this.octetStrings = Collections.unmodifiableList(octetStrings);
    }

    private SequenceOfOctetString(ASN1Sequence seq) {
        ArrayList<ASN1OctetString> items = new ArrayList<ASN1OctetString>();
        Iterator it = seq.iterator();
        while (it.hasNext()) {
            items.add(DEROctetString.getInstance(it.next()));
        }
        this.octetStrings = Collections.unmodifiableList(items);
    }

    public static SequenceOfOctetString getInstance(Object o) {
        if (o instanceof SequenceOfOctetString) {
            return (SequenceOfOctetString)((Object)o);
        }
        if (o != null) {
            return new SequenceOfOctetString(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public List<ASN1OctetString> getOctetStrings() {
        return this.octetStrings;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        for (int i = 0; i != this.octetStrings.size(); ++i) {
            v.add((ASN1Encodable)this.octetStrings.get(i));
        }
        return new DERSequence(v);
    }
}

