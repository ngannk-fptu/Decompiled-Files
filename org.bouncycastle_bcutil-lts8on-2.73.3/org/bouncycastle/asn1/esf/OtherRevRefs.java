/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.esf;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class OtherRevRefs
extends ASN1Object {
    private ASN1ObjectIdentifier otherRevRefType;
    private ASN1Encodable otherRevRefs;

    public static OtherRevRefs getInstance(Object obj) {
        if (obj instanceof OtherRevRefs) {
            return (OtherRevRefs)((Object)obj);
        }
        if (obj != null) {
            return new OtherRevRefs(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    private OtherRevRefs(ASN1Sequence seq) {
        if (seq.size() != 2) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        this.otherRevRefType = new ASN1ObjectIdentifier(((ASN1ObjectIdentifier)seq.getObjectAt(0)).getId());
        try {
            this.otherRevRefs = ASN1Primitive.fromByteArray((byte[])seq.getObjectAt(1).toASN1Primitive().getEncoded("DER"));
        }
        catch (IOException e) {
            throw new IllegalStateException();
        }
    }

    public OtherRevRefs(ASN1ObjectIdentifier otherRevRefType, ASN1Encodable otherRevRefs) {
        this.otherRevRefType = otherRevRefType;
        this.otherRevRefs = otherRevRefs;
    }

    public ASN1ObjectIdentifier getOtherRevRefType() {
        return this.otherRevRefType;
    }

    public ASN1Encodable getOtherRevRefs() {
        return this.otherRevRefs;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.otherRevRefType);
        v.add(this.otherRevRefs);
        return new DERSequence(v);
    }
}

