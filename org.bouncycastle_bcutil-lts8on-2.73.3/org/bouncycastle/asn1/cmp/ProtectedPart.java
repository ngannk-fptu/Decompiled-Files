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
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIHeader;

public class ProtectedPart
extends ASN1Object {
    private final PKIHeader header;
    private final PKIBody body;

    private ProtectedPart(ASN1Sequence seq) {
        this.header = PKIHeader.getInstance(seq.getObjectAt(0));
        this.body = PKIBody.getInstance(seq.getObjectAt(1));
    }

    public ProtectedPart(PKIHeader header, PKIBody body) {
        this.header = header;
        this.body = body;
    }

    public static ProtectedPart getInstance(Object o) {
        if (o instanceof ProtectedPart) {
            return (ProtectedPart)((Object)o);
        }
        if (o != null) {
            return new ProtectedPart(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public PKIHeader getHeader() {
        return this.header;
    }

    public PKIBody getBody() {
        return this.body;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.header);
        v.add((ASN1Encodable)this.body);
        return new DERSequence(v);
    }
}

