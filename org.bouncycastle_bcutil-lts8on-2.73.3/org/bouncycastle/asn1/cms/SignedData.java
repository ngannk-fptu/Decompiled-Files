/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.BERSequence
 *  org.bouncycastle.asn1.BERSet
 *  org.bouncycastle.asn1.BERTaggedObject
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.DLSequence
 */
package org.bouncycastle.asn1.cms;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignerInfo;

public class SignedData
extends ASN1Object {
    private static final ASN1Integer VERSION_1 = new ASN1Integer(1L);
    private static final ASN1Integer VERSION_3 = new ASN1Integer(3L);
    private static final ASN1Integer VERSION_4 = new ASN1Integer(4L);
    private static final ASN1Integer VERSION_5 = new ASN1Integer(5L);
    private final ASN1Integer version;
    private final ASN1Set digestAlgorithms;
    private final ContentInfo contentInfo;
    private final ASN1Set signerInfos;
    private final boolean digsBer;
    private final boolean sigsBer;
    private ASN1Set certificates;
    private ASN1Set crls;
    private boolean certsBer;
    private boolean crlsBer;

    public static SignedData getInstance(Object o) {
        if (o instanceof SignedData) {
            return (SignedData)((Object)o);
        }
        if (o != null) {
            return new SignedData(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public SignedData(ASN1Set digestAlgorithms, ContentInfo contentInfo, ASN1Set certificates, ASN1Set crls, ASN1Set signerInfos) {
        this.version = this.calculateVersion(contentInfo.getContentType(), certificates, crls, signerInfos);
        this.digestAlgorithms = digestAlgorithms;
        this.contentInfo = contentInfo;
        this.certificates = certificates;
        this.crls = crls;
        this.signerInfos = signerInfos;
        this.digsBer = digestAlgorithms instanceof BERSet;
        this.crlsBer = crls instanceof BERSet;
        this.certsBer = certificates instanceof BERSet;
        this.sigsBer = signerInfos instanceof BERSet;
    }

    private ASN1Integer calculateVersion(ASN1ObjectIdentifier contentOid, ASN1Set certs, ASN1Set crls, ASN1Set signerInfs) {
        Object obj;
        Enumeration en;
        boolean otherCert = false;
        boolean otherCrl = false;
        boolean attrCertV1Found = false;
        boolean attrCertV2Found = false;
        if (certs != null) {
            en = certs.getObjects();
            while (en.hasMoreElements()) {
                obj = en.nextElement();
                if (!(obj instanceof ASN1TaggedObject)) continue;
                ASN1TaggedObject tagged = ASN1TaggedObject.getInstance(obj);
                if (tagged.getTagNo() == 1) {
                    attrCertV1Found = true;
                    continue;
                }
                if (tagged.getTagNo() == 2) {
                    attrCertV2Found = true;
                    continue;
                }
                if (tagged.getTagNo() != 3) continue;
                otherCert = true;
            }
        }
        if (otherCert) {
            return new ASN1Integer(5L);
        }
        if (crls != null) {
            en = crls.getObjects();
            while (en.hasMoreElements()) {
                obj = en.nextElement();
                if (!(obj instanceof ASN1TaggedObject)) continue;
                otherCrl = true;
            }
        }
        if (otherCrl) {
            return VERSION_5;
        }
        if (attrCertV2Found) {
            return VERSION_4;
        }
        if (attrCertV1Found) {
            return VERSION_3;
        }
        if (this.checkForVersion3(signerInfs)) {
            return VERSION_3;
        }
        if (!CMSObjectIdentifiers.data.equals((ASN1Primitive)contentOid)) {
            return VERSION_3;
        }
        return VERSION_1;
    }

    private boolean checkForVersion3(ASN1Set signerInfs) {
        Enumeration e = signerInfs.getObjects();
        while (e.hasMoreElements()) {
            SignerInfo s = SignerInfo.getInstance(e.nextElement());
            if (!s.getVersion().hasValue(3)) continue;
            return true;
        }
        return false;
    }

    private SignedData(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        this.version = ASN1Integer.getInstance(e.nextElement());
        this.digestAlgorithms = (ASN1Set)e.nextElement();
        this.contentInfo = ContentInfo.getInstance(e.nextElement());
        ASN1Set sigInfs = null;
        while (e.hasMoreElements()) {
            ASN1Primitive o = (ASN1Primitive)e.nextElement();
            if (o instanceof ASN1TaggedObject) {
                ASN1TaggedObject tagged = (ASN1TaggedObject)o;
                switch (tagged.getTagNo()) {
                    case 0: {
                        this.certsBer = tagged instanceof BERTaggedObject;
                        this.certificates = ASN1Set.getInstance((ASN1TaggedObject)tagged, (boolean)false);
                        break;
                    }
                    case 1: {
                        this.crlsBer = tagged instanceof BERTaggedObject;
                        this.crls = ASN1Set.getInstance((ASN1TaggedObject)tagged, (boolean)false);
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("unknown tag value " + tagged.getTagNo());
                    }
                }
                continue;
            }
            if (!(o instanceof ASN1Set)) {
                throw new IllegalArgumentException("SET expected, not encountered");
            }
            sigInfs = (ASN1Set)o;
        }
        if (sigInfs == null) {
            throw new IllegalArgumentException("signerInfos not set");
        }
        this.signerInfos = sigInfs;
        this.digsBer = this.digestAlgorithms instanceof BERSet;
        this.sigsBer = this.signerInfos instanceof BERSet;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public ASN1Set getDigestAlgorithms() {
        return this.digestAlgorithms;
    }

    public ContentInfo getEncapContentInfo() {
        return this.contentInfo;
    }

    public ASN1Set getCertificates() {
        return this.certificates;
    }

    public ASN1Set getCRLs() {
        return this.crls;
    }

    public ASN1Set getSignerInfos() {
        return this.signerInfos;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(6);
        v.add((ASN1Encodable)this.version);
        v.add((ASN1Encodable)this.digestAlgorithms);
        v.add((ASN1Encodable)this.contentInfo);
        if (this.certificates != null) {
            if (this.certsBer) {
                v.add((ASN1Encodable)new BERTaggedObject(false, 0, (ASN1Encodable)this.certificates));
            } else {
                v.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.certificates));
            }
        }
        if (this.crls != null) {
            if (this.crlsBer) {
                v.add((ASN1Encodable)new BERTaggedObject(false, 1, (ASN1Encodable)this.crls));
            } else {
                v.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.crls));
            }
        }
        v.add((ASN1Encodable)this.signerInfos);
        if (!this.contentInfo.isDefiniteLength() || this.digsBer || this.sigsBer || this.crlsBer || this.certsBer) {
            return new BERSequence(v);
        }
        return new DLSequence(v);
    }
}

