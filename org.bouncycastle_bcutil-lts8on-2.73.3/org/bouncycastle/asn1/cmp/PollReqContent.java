/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.cmp;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class PollReqContent
extends ASN1Object {
    private final ASN1Sequence content;

    private PollReqContent(ASN1Sequence seq) {
        this.content = seq;
    }

    public PollReqContent(ASN1Integer certReqId) {
        this((ASN1Sequence)new DERSequence((ASN1Encodable)new DERSequence((ASN1Encodable)certReqId)));
    }

    public PollReqContent(ASN1Integer[] certReqIds) {
        this((ASN1Sequence)new DERSequence((ASN1Encodable[])PollReqContent.intsToSequence(certReqIds)));
    }

    public PollReqContent(BigInteger certReqId) {
        this(new ASN1Integer(certReqId));
    }

    public PollReqContent(BigInteger[] certReqIds) {
        this(PollReqContent.intsToASN1(certReqIds));
    }

    public static PollReqContent getInstance(Object o) {
        if (o instanceof PollReqContent) {
            return (PollReqContent)((Object)o);
        }
        if (o != null) {
            return new PollReqContent(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    private static ASN1Integer[] sequenceToASN1IntegerArray(ASN1Sequence seq) {
        ASN1Integer[] result = new ASN1Integer[seq.size()];
        for (int i = 0; i != result.length; ++i) {
            result[i] = ASN1Integer.getInstance((Object)seq.getObjectAt(i));
        }
        return result;
    }

    private static DERSequence[] intsToSequence(ASN1Integer[] ids) {
        DERSequence[] result = new DERSequence[ids.length];
        for (int i = 0; i != result.length; ++i) {
            result[i] = new DERSequence((ASN1Encodable)ids[i]);
        }
        return result;
    }

    private static ASN1Integer[] intsToASN1(BigInteger[] ids) {
        ASN1Integer[] result = new ASN1Integer[ids.length];
        for (int i = 0; i != result.length; ++i) {
            result[i] = new ASN1Integer(ids[i]);
        }
        return result;
    }

    public ASN1Integer[][] getCertReqIds() {
        ASN1Integer[][] result = new ASN1Integer[this.content.size()][];
        for (int i = 0; i != result.length; ++i) {
            result[i] = PollReqContent.sequenceToASN1IntegerArray((ASN1Sequence)this.content.getObjectAt(i));
        }
        return result;
    }

    public BigInteger[] getCertReqIdValues() {
        BigInteger[] result = new BigInteger[this.content.size()];
        for (int i = 0; i != result.length; ++i) {
            result[i] = ASN1Integer.getInstance((Object)ASN1Sequence.getInstance((Object)this.content.getObjectAt(i)).getObjectAt(0)).getValue();
        }
        return result;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}

