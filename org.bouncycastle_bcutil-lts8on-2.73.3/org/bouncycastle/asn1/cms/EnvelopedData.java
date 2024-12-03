/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.BERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.asn1.cms;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.Attributes;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.cms.OtherRecipientInfo;
import org.bouncycastle.asn1.cms.PasswordRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientInfo;

public class EnvelopedData
extends ASN1Object {
    private ASN1Integer version;
    private OriginatorInfo originatorInfo;
    private ASN1Set recipientInfos;
    private EncryptedContentInfo encryptedContentInfo;
    private ASN1Set unprotectedAttrs;

    public EnvelopedData(OriginatorInfo originatorInfo, ASN1Set recipientInfos, EncryptedContentInfo encryptedContentInfo, ASN1Set unprotectedAttrs) {
        this.version = new ASN1Integer((long)EnvelopedData.calculateVersion(originatorInfo, recipientInfos, unprotectedAttrs));
        this.originatorInfo = originatorInfo;
        this.recipientInfos = recipientInfos;
        this.encryptedContentInfo = encryptedContentInfo;
        this.unprotectedAttrs = unprotectedAttrs;
    }

    public EnvelopedData(OriginatorInfo originatorInfo, ASN1Set recipientInfos, EncryptedContentInfo encryptedContentInfo, Attributes unprotectedAttrs) {
        this.version = new ASN1Integer((long)EnvelopedData.calculateVersion(originatorInfo, recipientInfos, ASN1Set.getInstance((Object)((Object)unprotectedAttrs))));
        this.originatorInfo = originatorInfo;
        this.recipientInfos = recipientInfos;
        this.encryptedContentInfo = encryptedContentInfo;
        this.unprotectedAttrs = ASN1Set.getInstance((Object)((Object)unprotectedAttrs));
    }

    private EnvelopedData(ASN1Sequence seq) {
        int index = 0;
        this.version = (ASN1Integer)seq.getObjectAt(index++);
        ASN1Encodable tmp = seq.getObjectAt(index++);
        if (tmp instanceof ASN1TaggedObject) {
            this.originatorInfo = OriginatorInfo.getInstance((ASN1TaggedObject)tmp, false);
            tmp = seq.getObjectAt(index++);
        }
        this.recipientInfos = ASN1Set.getInstance((Object)tmp);
        this.encryptedContentInfo = EncryptedContentInfo.getInstance(seq.getObjectAt(index++));
        if (seq.size() > index) {
            this.unprotectedAttrs = ASN1Set.getInstance((ASN1TaggedObject)((ASN1TaggedObject)seq.getObjectAt(index)), (boolean)false);
        }
    }

    public static EnvelopedData getInstance(ASN1TaggedObject obj, boolean explicit) {
        return EnvelopedData.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public static EnvelopedData getInstance(Object obj) {
        if (obj instanceof EnvelopedData) {
            return (EnvelopedData)((Object)obj);
        }
        if (obj != null) {
            return new EnvelopedData(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public OriginatorInfo getOriginatorInfo() {
        return this.originatorInfo;
    }

    public ASN1Set getRecipientInfos() {
        return this.recipientInfos;
    }

    public EncryptedContentInfo getEncryptedContentInfo() {
        return this.encryptedContentInfo;
    }

    public ASN1Set getUnprotectedAttrs() {
        return this.unprotectedAttrs;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(5);
        v.add((ASN1Encodable)this.version);
        if (this.originatorInfo != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.originatorInfo));
        }
        v.add((ASN1Encodable)this.recipientInfos);
        v.add((ASN1Encodable)this.encryptedContentInfo);
        if (this.unprotectedAttrs != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.unprotectedAttrs));
        }
        return new BERSequence(v);
    }

    public static int calculateVersion(OriginatorInfo originatorInfo, ASN1Set recipientInfos, ASN1Set unprotectedAttrs) {
        Enumeration e = recipientInfos.getObjects();
        boolean nonZeroFound = false;
        boolean pwriOrOri = false;
        while (e.hasMoreElements()) {
            ASN1Encodable info;
            RecipientInfo ri = RecipientInfo.getInstance(e.nextElement());
            if (!ri.getVersion().hasValue(0)) {
                nonZeroFound = true;
            }
            if (!((info = ri.getInfo()) instanceof PasswordRecipientInfo) && !(info instanceof OtherRecipientInfo)) continue;
            pwriOrOri = true;
        }
        if (pwriOrOri) {
            return 3;
        }
        if (nonZeroFound) {
            return 2;
        }
        if (originatorInfo != null || unprotectedAttrs != null) {
            return 2;
        }
        return 0;
    }
}

