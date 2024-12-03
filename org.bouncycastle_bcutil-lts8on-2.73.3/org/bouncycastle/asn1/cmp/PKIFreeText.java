/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.ASN1UTF8String
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERUTF8String
 */
package org.bouncycastle.asn1.cmp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;

public class PKIFreeText
extends ASN1Object {
    ASN1Sequence strings;

    private PKIFreeText(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        while (e.hasMoreElements()) {
            if (e.nextElement() instanceof ASN1UTF8String) continue;
            throw new IllegalArgumentException("attempt to insert non UTF8 STRING into PKIFreeText");
        }
        this.strings = seq;
    }

    public PKIFreeText(ASN1UTF8String p) {
        this.strings = new DERSequence((ASN1Encodable)p);
    }

    public PKIFreeText(String p) {
        this((ASN1UTF8String)new DERUTF8String(p));
    }

    public PKIFreeText(ASN1UTF8String[] strs) {
        this.strings = new DERSequence((ASN1Encodable[])strs);
    }

    public PKIFreeText(String[] strs) {
        ASN1EncodableVector v = new ASN1EncodableVector(strs.length);
        for (int i = 0; i < strs.length; ++i) {
            v.add((ASN1Encodable)new DERUTF8String(strs[i]));
        }
        this.strings = new DERSequence(v);
    }

    public static PKIFreeText getInstance(ASN1TaggedObject obj, boolean explicit) {
        return PKIFreeText.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public static PKIFreeText getInstance(Object obj) {
        if (obj instanceof PKIFreeText) {
            return (PKIFreeText)((Object)obj);
        }
        if (obj != null) {
            return new PKIFreeText(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public int size() {
        return this.strings.size();
    }

    public ASN1UTF8String getStringAtUTF8(int i) {
        return (ASN1UTF8String)this.strings.getObjectAt(i);
    }

    public ASN1Primitive toASN1Primitive() {
        return this.strings;
    }
}

