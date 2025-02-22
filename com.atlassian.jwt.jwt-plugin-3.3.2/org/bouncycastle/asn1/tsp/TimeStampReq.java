/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.ASN1Boolean;
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

    public static TimeStampReq getInstance(Object object) {
        if (object instanceof TimeStampReq) {
            return (TimeStampReq)object;
        }
        if (object != null) {
            return new TimeStampReq(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    private TimeStampReq(ASN1Sequence aSN1Sequence) {
        int n = aSN1Sequence.size();
        int n2 = 0;
        this.version = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(n2));
        this.messageImprint = MessageImprint.getInstance(aSN1Sequence.getObjectAt(++n2));
        for (int i = ++n2; i < n; ++i) {
            if (aSN1Sequence.getObjectAt(i) instanceof ASN1ObjectIdentifier) {
                this.checkOption(this.tsaPolicy, i, 2);
                this.tsaPolicy = ASN1ObjectIdentifier.getInstance(aSN1Sequence.getObjectAt(i));
                continue;
            }
            if (aSN1Sequence.getObjectAt(i) instanceof ASN1Integer) {
                this.checkOption(this.nonce, i, 3);
                this.nonce = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(i));
                continue;
            }
            if (aSN1Sequence.getObjectAt(i) instanceof ASN1Boolean) {
                this.checkOption(this.certReq, i, 4);
                this.certReq = ASN1Boolean.getInstance(aSN1Sequence.getObjectAt(i));
                continue;
            }
            if (aSN1Sequence.getObjectAt(i) instanceof ASN1TaggedObject) {
                this.checkOption(this.extensions, i, 5);
                ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)aSN1Sequence.getObjectAt(i);
                if (aSN1TaggedObject.getTagNo() != 0) continue;
                this.extensions = Extensions.getInstance(aSN1TaggedObject, false);
                continue;
            }
            throw new IllegalArgumentException("unidentified structure in sequence");
        }
    }

    private void checkOption(Object object, int n, int n2) {
        if (object != null || n > n2) {
            throw new IllegalArgumentException("badly placed optional in sequence");
        }
    }

    public TimeStampReq(MessageImprint messageImprint, ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Integer aSN1Integer, ASN1Boolean aSN1Boolean, Extensions extensions) {
        this.version = new ASN1Integer(1L);
        this.messageImprint = messageImprint;
        this.tsaPolicy = aSN1ObjectIdentifier;
        this.nonce = aSN1Integer;
        this.certReq = aSN1Boolean;
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
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(6);
        aSN1EncodableVector.add(this.version);
        aSN1EncodableVector.add(this.messageImprint);
        if (this.tsaPolicy != null) {
            aSN1EncodableVector.add(this.tsaPolicy);
        }
        if (this.nonce != null) {
            aSN1EncodableVector.add(this.nonce);
        }
        if (this.certReq != null && this.certReq.isTrue()) {
            aSN1EncodableVector.add(this.certReq);
        }
        if (this.extensions != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 0, this.extensions));
        }
        return new DERSequence(aSN1EncodableVector);
    }
}

