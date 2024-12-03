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
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.asn1.cms;

import java.util.Enumeration;
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
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class AuthenticatedData
extends ASN1Object {
    private ASN1Integer version;
    private OriginatorInfo originatorInfo;
    private ASN1Set recipientInfos;
    private AlgorithmIdentifier macAlgorithm;
    private AlgorithmIdentifier digestAlgorithm;
    private ContentInfo encapsulatedContentInfo;
    private ASN1Set authAttrs;
    private ASN1OctetString mac;
    private ASN1Set unauthAttrs;

    public AuthenticatedData(OriginatorInfo originatorInfo, ASN1Set recipientInfos, AlgorithmIdentifier macAlgorithm, AlgorithmIdentifier digestAlgorithm, ContentInfo encapsulatedContent, ASN1Set authAttrs, ASN1OctetString mac, ASN1Set unauthAttrs) {
        if (!(digestAlgorithm == null && authAttrs == null || digestAlgorithm != null && authAttrs != null)) {
            throw new IllegalArgumentException("digestAlgorithm and authAttrs must be set together");
        }
        this.version = new ASN1Integer((long)AuthenticatedData.calculateVersion(originatorInfo));
        this.originatorInfo = originatorInfo;
        this.macAlgorithm = macAlgorithm;
        this.digestAlgorithm = digestAlgorithm;
        this.recipientInfos = recipientInfos;
        this.encapsulatedContentInfo = encapsulatedContent;
        this.authAttrs = authAttrs;
        this.mac = mac;
        this.unauthAttrs = unauthAttrs;
    }

    private AuthenticatedData(ASN1Sequence seq) {
        int index = 0;
        this.version = (ASN1Integer)seq.getObjectAt(index++);
        ASN1Encodable tmp = seq.getObjectAt(index++);
        if (tmp instanceof ASN1TaggedObject) {
            this.originatorInfo = OriginatorInfo.getInstance((ASN1TaggedObject)tmp, false);
            tmp = seq.getObjectAt(index++);
        }
        this.recipientInfos = ASN1Set.getInstance((Object)tmp);
        this.macAlgorithm = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(index++));
        if ((tmp = seq.getObjectAt(index++)) instanceof ASN1TaggedObject) {
            this.digestAlgorithm = AlgorithmIdentifier.getInstance((ASN1TaggedObject)((ASN1TaggedObject)tmp), (boolean)false);
            tmp = seq.getObjectAt(index++);
        }
        this.encapsulatedContentInfo = ContentInfo.getInstance(tmp);
        if ((tmp = seq.getObjectAt(index++)) instanceof ASN1TaggedObject) {
            this.authAttrs = ASN1Set.getInstance((ASN1TaggedObject)((ASN1TaggedObject)tmp), (boolean)false);
            tmp = seq.getObjectAt(index++);
        }
        this.mac = ASN1OctetString.getInstance((Object)tmp);
        if (seq.size() > index) {
            this.unauthAttrs = ASN1Set.getInstance((ASN1TaggedObject)((ASN1TaggedObject)seq.getObjectAt(index)), (boolean)false);
        }
    }

    public static AuthenticatedData getInstance(ASN1TaggedObject obj, boolean explicit) {
        return AuthenticatedData.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public static AuthenticatedData getInstance(Object obj) {
        if (obj instanceof AuthenticatedData) {
            return (AuthenticatedData)((Object)obj);
        }
        if (obj != null) {
            return new AuthenticatedData(ASN1Sequence.getInstance((Object)obj));
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

    public AlgorithmIdentifier getMacAlgorithm() {
        return this.macAlgorithm;
    }

    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.digestAlgorithm;
    }

    public ContentInfo getEncapsulatedContentInfo() {
        return this.encapsulatedContentInfo;
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
        ASN1EncodableVector v = new ASN1EncodableVector(9);
        v.add((ASN1Encodable)this.version);
        if (this.originatorInfo != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.originatorInfo));
        }
        v.add((ASN1Encodable)this.recipientInfos);
        v.add((ASN1Encodable)this.macAlgorithm);
        if (this.digestAlgorithm != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.digestAlgorithm));
        }
        v.add((ASN1Encodable)this.encapsulatedContentInfo);
        if (this.authAttrs != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 2, (ASN1Encodable)this.authAttrs));
        }
        v.add((ASN1Encodable)this.mac);
        if (this.unauthAttrs != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 3, (ASN1Encodable)this.unauthAttrs));
        }
        return new BERSequence(v);
    }

    public static int calculateVersion(OriginatorInfo origInfo) {
        ASN1TaggedObject tag;
        Object obj;
        if (origInfo == null) {
            return 0;
        }
        int ver = 0;
        Enumeration e = origInfo.getCertificates().getObjects();
        while (e.hasMoreElements()) {
            obj = e.nextElement();
            if (!(obj instanceof ASN1TaggedObject)) continue;
            tag = (ASN1TaggedObject)obj;
            if (tag.getTagNo() == 2) {
                ver = 1;
                continue;
            }
            if (tag.getTagNo() != 3) continue;
            ver = 3;
            break;
        }
        if (origInfo.getCRLs() != null) {
            e = origInfo.getCRLs().getObjects();
            while (e.hasMoreElements()) {
                obj = e.nextElement();
                if (!(obj instanceof ASN1TaggedObject) || (tag = (ASN1TaggedObject)obj).getTagNo() != 1) continue;
                ver = 3;
                break;
            }
        }
        return ver;
    }
}

