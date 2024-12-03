/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1IA5String
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.BERSequence
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.cms.Evidence;
import org.bouncycastle.asn1.cms.MetaData;

public class TimeStampedData
extends ASN1Object {
    private ASN1Integer version;
    private ASN1IA5String dataUri;
    private MetaData metaData;
    private ASN1OctetString content;
    private Evidence temporalEvidence;

    public TimeStampedData(ASN1IA5String dataUri, MetaData metaData, ASN1OctetString content, Evidence temporalEvidence) {
        this.version = new ASN1Integer(1L);
        this.dataUri = dataUri;
        this.metaData = metaData;
        this.content = content;
        this.temporalEvidence = temporalEvidence;
    }

    private TimeStampedData(ASN1Sequence seq) {
        this.version = ASN1Integer.getInstance((Object)seq.getObjectAt(0));
        int index = 1;
        if (seq.getObjectAt(index) instanceof ASN1IA5String) {
            this.dataUri = ASN1IA5String.getInstance((Object)seq.getObjectAt(index++));
        }
        if (seq.getObjectAt(index) instanceof MetaData || seq.getObjectAt(index) instanceof ASN1Sequence) {
            this.metaData = MetaData.getInstance(seq.getObjectAt(index++));
        }
        if (seq.getObjectAt(index) instanceof ASN1OctetString) {
            this.content = ASN1OctetString.getInstance((Object)seq.getObjectAt(index++));
        }
        this.temporalEvidence = Evidence.getInstance(seq.getObjectAt(index));
    }

    public static TimeStampedData getInstance(Object obj) {
        if (obj == null || obj instanceof TimeStampedData) {
            return (TimeStampedData)((Object)obj);
        }
        return new TimeStampedData(ASN1Sequence.getInstance((Object)obj));
    }

    public ASN1IA5String getDataUriIA5() {
        return this.dataUri;
    }

    public MetaData getMetaData() {
        return this.metaData;
    }

    public ASN1OctetString getContent() {
        return this.content;
    }

    public Evidence getTemporalEvidence() {
        return this.temporalEvidence;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(5);
        v.add((ASN1Encodable)this.version);
        if (this.dataUri != null) {
            v.add((ASN1Encodable)this.dataUri);
        }
        if (this.metaData != null) {
            v.add((ASN1Encodable)this.metaData);
        }
        if (this.content != null) {
            v.add((ASN1Encodable)this.content);
        }
        v.add((ASN1Encodable)this.temporalEvidence);
        return new BERSequence(v);
    }
}

