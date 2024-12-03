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
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.RevDetails;

public class RevReqContent
extends ASN1Object {
    private final ASN1Sequence content;

    private RevReqContent(ASN1Sequence seq) {
        this.content = seq;
    }

    public RevReqContent(RevDetails revDetails) {
        this.content = new DERSequence((ASN1Encodable)revDetails);
    }

    public RevReqContent(RevDetails[] revDetailsArray) {
        this.content = new DERSequence((ASN1Encodable[])revDetailsArray);
    }

    public static RevReqContent getInstance(Object o) {
        if (o instanceof RevReqContent) {
            return (RevReqContent)((Object)o);
        }
        if (o != null) {
            return new RevReqContent(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public RevDetails[] toRevDetailsArray() {
        RevDetails[] result = new RevDetails[this.content.size()];
        for (int i = 0; i != result.length; ++i) {
            result[i] = RevDetails.getInstance(this.content.getObjectAt(i));
        }
        return result;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}

