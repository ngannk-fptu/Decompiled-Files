/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.SubjectKeyIdentifier
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.OriginatorPublicKey;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;

public class OriginatorIdentifierOrKey
extends ASN1Object
implements ASN1Choice {
    private ASN1Encodable id;

    public OriginatorIdentifierOrKey(IssuerAndSerialNumber id) {
        this.id = id;
    }

    public OriginatorIdentifierOrKey(SubjectKeyIdentifier id) {
        this.id = new DERTaggedObject(false, 0, (ASN1Encodable)id);
    }

    public OriginatorIdentifierOrKey(OriginatorPublicKey id) {
        this.id = new DERTaggedObject(false, 1, (ASN1Encodable)id);
    }

    public static OriginatorIdentifierOrKey getInstance(ASN1TaggedObject o, boolean explicit) {
        if (!explicit) {
            throw new IllegalArgumentException("Can't implicitly tag OriginatorIdentifierOrKey");
        }
        return OriginatorIdentifierOrKey.getInstance(o.getExplicitBaseObject());
    }

    public static OriginatorIdentifierOrKey getInstance(Object o) {
        if (o == null || o instanceof OriginatorIdentifierOrKey) {
            return (OriginatorIdentifierOrKey)((Object)o);
        }
        if (o instanceof IssuerAndSerialNumber) {
            return new OriginatorIdentifierOrKey((IssuerAndSerialNumber)((Object)o));
        }
        if (o instanceof ASN1Sequence) {
            return new OriginatorIdentifierOrKey(IssuerAndSerialNumber.getInstance(o));
        }
        if (o instanceof ASN1TaggedObject) {
            ASN1TaggedObject taggedObject = (ASN1TaggedObject)o;
            if (taggedObject.hasContextTag(0)) {
                return new OriginatorIdentifierOrKey(SubjectKeyIdentifier.getInstance((ASN1TaggedObject)taggedObject, (boolean)false));
            }
            if (taggedObject.hasContextTag(1)) {
                return new OriginatorIdentifierOrKey(OriginatorPublicKey.getInstance(taggedObject, false));
            }
        }
        throw new IllegalArgumentException("Invalid OriginatorIdentifierOrKey: " + o.getClass().getName());
    }

    public ASN1Encodable getId() {
        return this.id;
    }

    public IssuerAndSerialNumber getIssuerAndSerialNumber() {
        if (this.id instanceof IssuerAndSerialNumber) {
            return (IssuerAndSerialNumber)this.id;
        }
        return null;
    }

    public SubjectKeyIdentifier getSubjectKeyIdentifier() {
        ASN1TaggedObject taggedObject;
        if (this.id instanceof ASN1TaggedObject && (taggedObject = (ASN1TaggedObject)this.id).hasContextTag(0)) {
            return SubjectKeyIdentifier.getInstance((ASN1TaggedObject)taggedObject, (boolean)false);
        }
        return null;
    }

    public OriginatorPublicKey getOriginatorKey() {
        ASN1TaggedObject taggedObject;
        if (this.id instanceof ASN1TaggedObject && (taggedObject = (ASN1TaggedObject)this.id).hasContextTag(1)) {
            return OriginatorPublicKey.getInstance(taggedObject, false);
        }
        return null;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.id.toASN1Primitive();
    }
}

