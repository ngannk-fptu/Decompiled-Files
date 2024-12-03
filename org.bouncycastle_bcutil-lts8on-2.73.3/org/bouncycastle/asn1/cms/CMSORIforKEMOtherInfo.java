/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CMSORIforKEMOtherInfo
extends ASN1Object {
    private final AlgorithmIdentifier wrap;
    private final int kekLength;
    private final byte[] ukm;

    public CMSORIforKEMOtherInfo(AlgorithmIdentifier wrap, int kekLength) {
        this(wrap, kekLength, null);
    }

    public CMSORIforKEMOtherInfo(AlgorithmIdentifier wrap, int kekLength, byte[] ukm) {
        this.wrap = wrap;
        this.kekLength = kekLength;
        this.ukm = ukm;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add((ASN1Encodable)this.wrap);
        v.add((ASN1Encodable)new ASN1Integer((long)this.kekLength));
        if (this.ukm != null) {
            v.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)new DEROctetString(this.ukm)));
        }
        return new DERSequence(v);
    }
}

