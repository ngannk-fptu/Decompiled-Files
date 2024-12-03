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
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.asn1.tsp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class PartialHashtree
extends ASN1Object {
    private final ASN1Sequence values;

    public static PartialHashtree getInstance(Object obj) {
        if (obj instanceof PartialHashtree) {
            return (PartialHashtree)((Object)obj);
        }
        if (obj != null) {
            return new PartialHashtree(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    private PartialHashtree(ASN1Sequence values) {
        for (int i = 0; i != values.size(); ++i) {
            if (values.getObjectAt(i) instanceof ASN1OctetString) continue;
            throw new IllegalArgumentException("unknown object in constructor: " + values.getObjectAt(i).getClass().getName());
        }
        this.values = values;
    }

    public PartialHashtree(byte[] values) {
        this(new byte[][]{values});
    }

    public PartialHashtree(byte[][] values) {
        ASN1EncodableVector v = new ASN1EncodableVector(values.length);
        for (int i = 0; i != values.length; ++i) {
            v.add((ASN1Encodable)new DEROctetString(Arrays.clone((byte[])values[i])));
        }
        this.values = new DERSequence(v);
    }

    public int getValueCount() {
        return this.values.size();
    }

    public byte[][] getValues() {
        byte[][] rv = new byte[this.values.size()][];
        for (int i = 0; i != rv.length; ++i) {
            rv[i] = Arrays.clone((byte[])ASN1OctetString.getInstance((Object)this.values.getObjectAt(i)).getOctets());
        }
        return rv;
    }

    public boolean containsHash(byte[] hash) {
        Enumeration hashes = this.values.getObjects();
        while (hashes.hasMoreElements()) {
            byte[] currentHash = ASN1OctetString.getInstance(hashes.nextElement()).getOctets();
            if (!Arrays.constantTimeAreEqual((byte[])hash, (byte[])currentHash)) continue;
            return true;
        }
        return false;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.values;
    }
}

