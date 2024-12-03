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
import org.bouncycastle.asn1.cmp.InfoTypeAndValue;

public class GenMsgContent
extends ASN1Object {
    private final ASN1Sequence content;

    private GenMsgContent(ASN1Sequence seq) {
        this.content = seq;
    }

    public GenMsgContent(InfoTypeAndValue itv) {
        this.content = new DERSequence((ASN1Encodable)itv);
    }

    public GenMsgContent(InfoTypeAndValue[] itvs) {
        this.content = new DERSequence((ASN1Encodable[])itvs);
    }

    public static GenMsgContent getInstance(Object o) {
        if (o instanceof GenMsgContent) {
            return (GenMsgContent)((Object)o);
        }
        if (o != null) {
            return new GenMsgContent(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public InfoTypeAndValue[] toInfoTypeAndValueArray() {
        InfoTypeAndValue[] result = new InfoTypeAndValue[this.content.size()];
        for (int i = 0; i != result.length; ++i) {
            result[i] = InfoTypeAndValue.getInstance(this.content.getObjectAt(i));
        }
        return result;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}

