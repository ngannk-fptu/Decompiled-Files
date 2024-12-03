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
 *  org.bouncycastle.asn1.ASN1UTF8String
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.ess;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERSequence;

public class ContentHints
extends ASN1Object {
    private ASN1UTF8String contentDescription;
    private ASN1ObjectIdentifier contentType;

    public static ContentHints getInstance(Object o) {
        if (o instanceof ContentHints) {
            return (ContentHints)((Object)o);
        }
        if (o != null) {
            return new ContentHints(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    private ContentHints(ASN1Sequence seq) {
        ASN1Encodable field = seq.getObjectAt(0);
        if (field.toASN1Primitive() instanceof ASN1UTF8String) {
            this.contentDescription = ASN1UTF8String.getInstance((Object)field);
            this.contentType = ASN1ObjectIdentifier.getInstance((Object)seq.getObjectAt(1));
        } else {
            this.contentType = ASN1ObjectIdentifier.getInstance((Object)seq.getObjectAt(0));
        }
    }

    public ContentHints(ASN1ObjectIdentifier contentType) {
        this.contentType = contentType;
        this.contentDescription = null;
    }

    public ContentHints(ASN1ObjectIdentifier contentType, ASN1UTF8String contentDescription) {
        this.contentType = contentType;
        this.contentDescription = contentDescription;
    }

    public ASN1ObjectIdentifier getContentType() {
        return this.contentType;
    }

    public ASN1UTF8String getContentDescriptionUTF8() {
        return this.contentDescription;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        if (this.contentDescription != null) {
            v.add((ASN1Encodable)this.contentDescription);
        }
        v.add((ASN1Encodable)this.contentType);
        return new DERSequence(v);
    }
}

