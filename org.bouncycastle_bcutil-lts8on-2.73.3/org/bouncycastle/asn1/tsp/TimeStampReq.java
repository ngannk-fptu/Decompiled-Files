/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Boolean
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.Extensions
 */
package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.tsp.MessageImprint;
import org.bouncycastle.asn1.x509.Extensions;

public class TimeStampReq
extends ASN1Object {
    ASN1Integer version;
    MessageImprint messageImprint;
    ASN1ObjectIdentifier tsaPolicy;
    ASN1Integer nonce;
    ASN1Boolean certReq;
    Extensions extensions;

    public static TimeStampReq getInstance(Object o) {
        if (o instanceof TimeStampReq) {
            return (TimeStampReq)((Object)o);
        }
        if (o != null) {
            return new TimeStampReq(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    private TimeStampReq(ASN1Sequence seq) {
        int nbObjects = seq.size();
        int seqStart = 0;
        this.version = ASN1Integer.getInstance((Object)seq.getObjectAt(seqStart));
        this.messageImprint = MessageImprint.getInstance(seq.getObjectAt(++seqStart));
        for (int opt = ++seqStart; opt < nbObjects; ++opt) {
            if (seq.getObjectAt(opt) instanceof ASN1ObjectIdentifier) {
                this.checkOption(this.tsaPolicy, opt, 2);
                this.tsaPolicy = ASN1ObjectIdentifier.getInstance((Object)seq.getObjectAt(opt));
                continue;
            }
            if (seq.getObjectAt(opt) instanceof ASN1Integer) {
                this.checkOption(this.nonce, opt, 3);
                this.nonce = ASN1Integer.getInstance((Object)seq.getObjectAt(opt));
                continue;
            }
            if (seq.getObjectAt(opt) instanceof ASN1Boolean) {
                this.checkOption(this.certReq, opt, 4);
                this.certReq = ASN1Boolean.getInstance((Object)seq.getObjectAt(opt));
                continue;
            }
            if (seq.getObjectAt(opt) instanceof ASN1TaggedObject) {
                this.checkOption(this.extensions, opt, 5);
                ASN1TaggedObject tagged = (ASN1TaggedObject)seq.getObjectAt(opt);
                if (tagged.getTagNo() != 0) continue;
                this.extensions = Extensions.getInstance((ASN1TaggedObject)tagged, (boolean)false);
                continue;
            }
            throw new IllegalArgumentException("unidentified structure in sequence");
        }
    }

    private void checkOption(Object o, int index, int maxOption) {
        if (o != null || index > maxOption) {
            throw new IllegalArgumentException("badly placed optional in sequence");
        }
    }

    public TimeStampReq(MessageImprint messageImprint, ASN1ObjectIdentifier tsaPolicy, ASN1Integer nonce, ASN1Boolean certReq, Extensions extensions) {
        this.version = new ASN1Integer(1L);
        this.messageImprint = messageImprint;
        this.tsaPolicy = tsaPolicy;
        this.nonce = nonce;
        this.certReq = certReq;
        this.extensions = extensions;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public MessageImprint getMessageImprint() {
        return this.messageImprint;
    }

    public ASN1ObjectIdentifier getReqPolicy() {
        return this.tsaPolicy;
    }

    public ASN1Integer getNonce() {
        return this.nonce;
    }

    public ASN1Boolean getCertReq() {
        if (this.certReq == null) {
            return ASN1Boolean.FALSE;
        }
        return this.certReq;
    }

    public Extensions getExtensions() {
        return this.extensions;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(6);
        v.add((ASN1Encodable)this.version);
        v.add((ASN1Encodable)this.messageImprint);
        if (this.tsaPolicy != null) {
            v.add((ASN1Encodable)this.tsaPolicy);
        }
        if (this.nonce != null) {
            v.add((ASN1Encodable)this.nonce);
        }
        if (this.certReq != null && this.certReq.isTrue()) {
            v.add((ASN1Encodable)this.certReq);
        }
        if (this.extensions != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.extensions));
        }
        return new DERSequence(v);
    }
}

