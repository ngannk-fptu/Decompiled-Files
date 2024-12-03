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
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.cms;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.TimeStampAndCRL;

public class TimeStampTokenEvidence
extends ASN1Object {
    private TimeStampAndCRL[] timeStampAndCRLs;

    public TimeStampTokenEvidence(TimeStampAndCRL[] timeStampAndCRLs) {
        this.timeStampAndCRLs = this.copy(timeStampAndCRLs);
    }

    public TimeStampTokenEvidence(TimeStampAndCRL timeStampAndCRL) {
        this.timeStampAndCRLs = new TimeStampAndCRL[1];
        this.timeStampAndCRLs[0] = timeStampAndCRL;
    }

    private TimeStampTokenEvidence(ASN1Sequence seq) {
        this.timeStampAndCRLs = new TimeStampAndCRL[seq.size()];
        int count = 0;
        Enumeration en = seq.getObjects();
        while (en.hasMoreElements()) {
            this.timeStampAndCRLs[count++] = TimeStampAndCRL.getInstance(en.nextElement());
        }
    }

    public static TimeStampTokenEvidence getInstance(ASN1TaggedObject tagged, boolean explicit) {
        return TimeStampTokenEvidence.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)tagged, (boolean)explicit));
    }

    public static TimeStampTokenEvidence getInstance(Object obj) {
        if (obj instanceof TimeStampTokenEvidence) {
            return (TimeStampTokenEvidence)((Object)obj);
        }
        if (obj != null) {
            return new TimeStampTokenEvidence(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public TimeStampAndCRL[] toTimeStampAndCRLArray() {
        return this.copy(this.timeStampAndCRLs);
    }

    private TimeStampAndCRL[] copy(TimeStampAndCRL[] tsAndCrls) {
        TimeStampAndCRL[] tmp = new TimeStampAndCRL[tsAndCrls.length];
        System.arraycopy(tsAndCrls, 0, tmp, 0, tmp.length);
        return tmp;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(this.timeStampAndCRLs.length);
        for (int i = 0; i != this.timeStampAndCRLs.length; ++i) {
            v.add((ASN1Encodable)this.timeStampAndCRLs[i]);
        }
        return new DERSequence(v);
    }
}

