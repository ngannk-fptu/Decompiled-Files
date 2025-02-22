/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.RevDetails;

public class RevReqContent
extends ASN1Object {
    private ASN1Sequence content;

    private RevReqContent(ASN1Sequence aSN1Sequence) {
        this.content = aSN1Sequence;
    }

    public static RevReqContent getInstance(Object object) {
        if (object instanceof RevReqContent) {
            return (RevReqContent)object;
        }
        if (object != null) {
            return new RevReqContent(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public RevReqContent(RevDetails revDetails) {
        this.content = new DERSequence(revDetails);
    }

    public RevReqContent(RevDetails[] revDetailsArray) {
        this.content = new DERSequence(revDetailsArray);
    }

    public RevDetails[] toRevDetailsArray() {
        RevDetails[] revDetailsArray = new RevDetails[this.content.size()];
        for (int i = 0; i != revDetailsArray.length; ++i) {
            revDetailsArray[i] = RevDetails.getInstance(this.content.getObjectAt(i));
        }
        return revDetailsArray;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}

