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
import org.bouncycastle.asn1.cmp.PKIMessage;

public class PKIMessages
extends ASN1Object {
    private final ASN1Sequence content;

    protected PKIMessages(ASN1Sequence seq) {
        this.content = seq;
    }

    public PKIMessages(PKIMessage msg) {
        this.content = new DERSequence((ASN1Encodable)msg);
    }

    public PKIMessages(PKIMessage[] msgs) {
        this.content = new DERSequence((ASN1Encodable[])msgs);
    }

    public static PKIMessages getInstance(Object o) {
        if (o instanceof PKIMessages) {
            return (PKIMessages)((Object)o);
        }
        if (o != null) {
            return new PKIMessages(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public PKIMessage[] toPKIMessageArray() {
        PKIMessage[] result = new PKIMessage[this.content.size()];
        for (int i = 0; i != result.length; ++i) {
            result[i] = PKIMessage.getInstance(this.content.getObjectAt(i));
        }
        return result;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}

