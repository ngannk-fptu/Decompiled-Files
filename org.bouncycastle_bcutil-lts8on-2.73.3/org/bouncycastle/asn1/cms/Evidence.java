/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.TimeStampTokenEvidence;
import org.bouncycastle.asn1.tsp.EvidenceRecord;

public class Evidence
extends ASN1Object
implements ASN1Choice {
    private TimeStampTokenEvidence tstEvidence;
    private EvidenceRecord ersEvidence;
    private ASN1Sequence otherEvidence;

    public Evidence(TimeStampTokenEvidence tstEvidence) {
        this.tstEvidence = tstEvidence;
    }

    public Evidence(EvidenceRecord ersEvidence) {
        this.ersEvidence = ersEvidence;
    }

    private Evidence(ASN1TaggedObject tagged) {
        if (tagged.getTagNo() == 0) {
            this.tstEvidence = TimeStampTokenEvidence.getInstance(tagged, false);
        } else if (tagged.getTagNo() == 1) {
            this.ersEvidence = EvidenceRecord.getInstance(tagged, false);
        } else if (tagged.getTagNo() == 2) {
            this.otherEvidence = ASN1Sequence.getInstance((ASN1TaggedObject)tagged, (boolean)false);
        } else {
            throw new IllegalArgumentException("unknown tag in Evidence");
        }
    }

    public static Evidence getInstance(Object obj) {
        if (obj == null || obj instanceof Evidence) {
            return (Evidence)((Object)obj);
        }
        if (obj instanceof ASN1TaggedObject) {
            return new Evidence(ASN1TaggedObject.getInstance((Object)obj, (int)128));
        }
        throw new IllegalArgumentException("unknown object in getInstance");
    }

    public static Evidence getInstance(ASN1TaggedObject obj, boolean explicit) {
        if (!explicit) {
            throw new IllegalArgumentException("choice item must be explicitly tagged");
        }
        return Evidence.getInstance(obj.getExplicitBaseObject());
    }

    public TimeStampTokenEvidence getTstEvidence() {
        return this.tstEvidence;
    }

    public EvidenceRecord getErsEvidence() {
        return this.ersEvidence;
    }

    public ASN1Primitive toASN1Primitive() {
        if (this.tstEvidence != null) {
            return new DERTaggedObject(false, 0, (ASN1Encodable)this.tstEvidence);
        }
        if (this.ersEvidence != null) {
            return new DERTaggedObject(false, 1, (ASN1Encodable)this.ersEvidence);
        }
        return new DERTaggedObject(false, 2, (ASN1Encodable)this.otherEvidence);
    }
}

