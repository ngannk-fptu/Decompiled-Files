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
package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.crmf.CertReqMsg;

public class CertReqMessages
extends ASN1Object {
    private ASN1Sequence content;

    private CertReqMessages(ASN1Sequence seq) {
        this.content = seq;
    }

    public static CertReqMessages getInstance(Object o) {
        if (o instanceof CertReqMessages) {
            return (CertReqMessages)((Object)o);
        }
        if (o != null) {
            return new CertReqMessages(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public CertReqMessages(CertReqMsg msg) {
        this.content = new DERSequence((ASN1Encodable)msg);
    }

    public CertReqMessages(CertReqMsg[] msgs) {
        this.content = new DERSequence((ASN1Encodable[])msgs);
    }

    public CertReqMsg[] toCertReqMsgArray() {
        CertReqMsg[] result = new CertReqMsg[this.content.size()];
        for (int i = 0; i != result.length; ++i) {
            result[i] = CertReqMsg.getInstance(this.content.getObjectAt(i));
        }
        return result;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}

