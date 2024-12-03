/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Boolean
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1GeneralizedTime
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.Extensions
 *  org.bouncycastle.asn1.x509.GeneralName
 */
package org.bouncycastle.asn1.tsp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.tsp.Accuracy;
import org.bouncycastle.asn1.tsp.MessageImprint;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;

public class TSTInfo
extends ASN1Object {
    private ASN1Integer version;
    private ASN1ObjectIdentifier tsaPolicyId;
    private MessageImprint messageImprint;
    private ASN1Integer serialNumber;
    private ASN1GeneralizedTime genTime;
    private Accuracy accuracy;
    private ASN1Boolean ordering;
    private ASN1Integer nonce;
    private GeneralName tsa;
    private Extensions extensions;

    public static TSTInfo getInstance(Object o) {
        if (o instanceof TSTInfo) {
            return (TSTInfo)((Object)o);
        }
        if (o != null) {
            return new TSTInfo(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    private TSTInfo(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        this.version = ASN1Integer.getInstance(e.nextElement());
        this.tsaPolicyId = ASN1ObjectIdentifier.getInstance(e.nextElement());
        this.messageImprint = MessageImprint.getInstance(e.nextElement());
        this.serialNumber = ASN1Integer.getInstance(e.nextElement());
        this.genTime = ASN1GeneralizedTime.getInstance(e.nextElement());
        this.ordering = ASN1Boolean.getInstance((boolean)false);
        while (e.hasMoreElements()) {
            ASN1Object o = (ASN1Object)e.nextElement();
            if (o instanceof ASN1TaggedObject) {
                ASN1TaggedObject tagged = (ASN1TaggedObject)o;
                switch (tagged.getTagNo()) {
                    case 0: {
                        this.tsa = GeneralName.getInstance((ASN1TaggedObject)tagged, (boolean)true);
                        break;
                    }
                    case 1: {
                        this.extensions = Extensions.getInstance((ASN1TaggedObject)tagged, (boolean)false);
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Unknown tag value " + tagged.getTagNo());
                    }
                }
                continue;
            }
            if (o instanceof ASN1Sequence || o instanceof Accuracy) {
                this.accuracy = Accuracy.getInstance(o);
                continue;
            }
            if (o instanceof ASN1Boolean) {
                this.ordering = ASN1Boolean.getInstance((Object)o);
                continue;
            }
            if (!(o instanceof ASN1Integer)) continue;
            this.nonce = ASN1Integer.getInstance((Object)o);
        }
    }

    public TSTInfo(ASN1ObjectIdentifier tsaPolicyId, MessageImprint messageImprint, ASN1Integer serialNumber, ASN1GeneralizedTime genTime, Accuracy accuracy, ASN1Boolean ordering, ASN1Integer nonce, GeneralName tsa, Extensions extensions) {
        this.version = new ASN1Integer(1L);
        this.tsaPolicyId = tsaPolicyId;
        this.messageImprint = messageImprint;
        this.serialNumber = serialNumber;
        this.genTime = genTime;
        this.accuracy = accuracy;
        this.ordering = ordering;
        this.nonce = nonce;
        this.tsa = tsa;
        this.extensions = extensions;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public MessageImprint getMessageImprint() {
        return this.messageImprint;
    }

    public ASN1ObjectIdentifier getPolicy() {
        return this.tsaPolicyId;
    }

    public ASN1Integer getSerialNumber() {
        return this.serialNumber;
    }

    public Accuracy getAccuracy() {
        return this.accuracy;
    }

    public ASN1GeneralizedTime getGenTime() {
        return this.genTime;
    }

    public ASN1Boolean getOrdering() {
        return this.ordering;
    }

    public ASN1Integer getNonce() {
        return this.nonce;
    }

    public GeneralName getTsa() {
        return this.tsa;
    }

    public Extensions getExtensions() {
        return this.extensions;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector seq = new ASN1EncodableVector(10);
        seq.add((ASN1Encodable)this.version);
        seq.add((ASN1Encodable)this.tsaPolicyId);
        seq.add((ASN1Encodable)this.messageImprint);
        seq.add((ASN1Encodable)this.serialNumber);
        seq.add((ASN1Encodable)this.genTime);
        if (this.accuracy != null) {
            seq.add((ASN1Encodable)this.accuracy);
        }
        if (this.ordering != null && this.ordering.isTrue()) {
            seq.add((ASN1Encodable)this.ordering);
        }
        if (this.nonce != null) {
            seq.add((ASN1Encodable)this.nonce);
        }
        if (this.tsa != null) {
            seq.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.tsa));
        }
        if (this.extensions != null) {
            seq.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.extensions));
        }
        return new DERSequence(seq);
    }
}

