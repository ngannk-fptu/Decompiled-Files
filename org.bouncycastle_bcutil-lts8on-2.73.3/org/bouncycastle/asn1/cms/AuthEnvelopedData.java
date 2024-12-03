/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.BERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.OriginatorInfo;

public class AuthEnvelopedData
extends ASN1Object {
    private ASN1Integer version;
    private OriginatorInfo originatorInfo;
    private ASN1Set recipientInfos;
    private EncryptedContentInfo authEncryptedContentInfo;
    private ASN1Set authAttrs;
    private ASN1OctetString mac;
    private ASN1Set unauthAttrs;

    public AuthEnvelopedData(OriginatorInfo originatorInfo, ASN1Set recipientInfos, EncryptedContentInfo authEncryptedContentInfo, ASN1Set authAttrs, ASN1OctetString mac, ASN1Set unauthAttrs) {
        this.version = new ASN1Integer(0L);
        this.originatorInfo = originatorInfo;
        this.recipientInfos = recipientInfos;
        if (this.recipientInfos.size() == 0) {
            throw new IllegalArgumentException("AuthEnvelopedData requires at least 1 RecipientInfo");
        }
        this.authEncryptedContentInfo = authEncryptedContentInfo;
        this.authAttrs = authAttrs;
        if (!(authEncryptedContentInfo.getContentType().equals((ASN1Primitive)CMSObjectIdentifiers.data) || authAttrs != null && authAttrs.size() != 0)) {
            throw new IllegalArgumentException("authAttrs must be present with non-data content");
        }
        this.mac = mac;
        this.unauthAttrs = unauthAttrs;
    }

    private AuthEnvelopedData(ASN1Sequence seq) {
        int index = 0;
        ASN1Primitive tmp = seq.getObjectAt(index++).toASN1Primitive();
        this.version = ASN1Integer.getInstance((Object)tmp);
        if (!this.version.hasValue(0)) {
            throw new IllegalArgumentException("AuthEnvelopedData version number must be 0");
        }
        if ((tmp = seq.getObjectAt(index++).toASN1Primitive()) instanceof ASN1TaggedObject) {
            this.originatorInfo = OriginatorInfo.getInstance((ASN1TaggedObject)tmp, false);
            tmp = seq.getObjectAt(index++).toASN1Primitive();
        }
        this.recipientInfos = ASN1Set.getInstance((Object)tmp);
        if (this.recipientInfos.size() == 0) {
            throw new IllegalArgumentException("AuthEnvelopedData requires at least 1 RecipientInfo");
        }
        tmp = seq.getObjectAt(index++).toASN1Primitive();
        this.authEncryptedContentInfo = EncryptedContentInfo.getInstance(tmp);
        if ((tmp = seq.getObjectAt(index++).toASN1Primitive()) instanceof ASN1TaggedObject) {
            this.authAttrs = ASN1Set.getInstance((ASN1TaggedObject)((ASN1TaggedObject)tmp), (boolean)false);
            tmp = seq.getObjectAt(index++).toASN1Primitive();
        } else if (!(this.authEncryptedContentInfo.getContentType().equals((ASN1Primitive)CMSObjectIdentifiers.data) || this.authAttrs != null && this.authAttrs.size() != 0)) {
            throw new IllegalArgumentException("authAttrs must be present with non-data content");
        }
        this.mac = ASN1OctetString.getInstance((Object)tmp);
        if (seq.size() > index) {
            tmp = seq.getObjectAt(index).toASN1Primitive();
            this.unauthAttrs = ASN1Set.getInstance((ASN1TaggedObject)((ASN1TaggedObject)tmp), (boolean)false);
        }
    }

    public static AuthEnvelopedData getInstance(ASN1TaggedObject obj, boolean explicit) {
        return AuthEnvelopedData.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public static AuthEnvelopedData getInstance(Object obj) {
        if (obj instanceof AuthEnvelopedData) {
            return (AuthEnvelopedData)((Object)obj);
        }
        if (obj != null) {
            return new AuthEnvelopedData(ASN1Sequence.getInstance((Object)obj));
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

    public EncryptedContentInfo getAuthEncryptedContentInfo() {
        return this.authEncryptedContentInfo;
    }

    public ASN1Set getAuthAttrs() {
        return this.authAttrs;
    }

    public ASN1OctetString getMac() {
        return this.mac;
    }

    public ASN1Set getUnauthAttrs() {
        return this.unauthAttrs;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(7);
        v.add((ASN1Encodable)this.version);
        if (this.originatorInfo != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.originatorInfo));
        }
        v.add((ASN1Encodable)this.recipientInfos);
        v.add((ASN1Encodable)this.authEncryptedContentInfo);
        if (this.authAttrs != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.authAttrs));
        }
        v.add((ASN1Encodable)this.mac);
        if (this.unauthAttrs != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 2, (ASN1Encodable)this.unauthAttrs));
        }
        return new BERSequence(v);
    }
}

