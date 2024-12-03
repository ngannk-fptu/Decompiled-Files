/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.BERSequence
 *  org.bouncycastle.asn1.BERTaggedObject
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DLSequence
 *  org.bouncycastle.asn1.DLTaggedObject
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.DLTaggedObject;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;

public class ContentInfo
extends ASN1Object
implements CMSObjectIdentifiers {
    private final ASN1ObjectIdentifier contentType;
    private final ASN1Encodable content;
    private final boolean isDefiniteLength;

    public static ContentInfo getInstance(Object obj) {
        if (obj instanceof ContentInfo) {
            return (ContentInfo)obj;
        }
        if (obj != null) {
            return new ContentInfo(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public static ContentInfo getInstance(ASN1TaggedObject obj, boolean explicit) {
        return ContentInfo.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    private ContentInfo(ASN1Sequence seq) {
        if (seq.size() < 1 || seq.size() > 2) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        this.contentType = (ASN1ObjectIdentifier)seq.getObjectAt(0);
        if (seq.size() > 1) {
            ASN1TaggedObject tagged = ASN1TaggedObject.getInstance((Object)seq.getObjectAt(1), (int)128);
            if (!tagged.isExplicit() || tagged.getTagNo() != 0) {
                throw new IllegalArgumentException("Bad tag for 'content'");
            }
            this.content = tagged.getExplicitBaseObject();
        } else {
            this.content = null;
        }
        this.isDefiniteLength = !(seq instanceof BERSequence);
    }

    public ContentInfo(ASN1ObjectIdentifier contentType, ASN1Encodable content) {
        ASN1Primitive prim;
        this.contentType = contentType;
        this.content = content;
        this.isDefiniteLength = content != null ? (prim = content.toASN1Primitive()) instanceof DEROctetString || prim instanceof DLSequence || prim instanceof DERSequence : true;
    }

    public ASN1ObjectIdentifier getContentType() {
        return this.contentType;
    }

    public ASN1Encodable getContent() {
        return this.content;
    }

    public boolean isDefiniteLength() {
        return this.isDefiniteLength;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.contentType);
        if (this.content != null) {
            if (this.isDefiniteLength) {
                v.add((ASN1Encodable)new DLTaggedObject(0, this.content));
            } else {
                v.add((ASN1Encodable)new BERTaggedObject(0, this.content));
            }
        }
        return this.isDefiniteLength ? new DLSequence(v) : new BERSequence(v);
    }
}

