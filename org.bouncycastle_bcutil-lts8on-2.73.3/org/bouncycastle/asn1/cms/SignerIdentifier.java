/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;

public class SignerIdentifier
extends ASN1Object
implements ASN1Choice {
    private ASN1Encodable id;

    public SignerIdentifier(IssuerAndSerialNumber id) {
        this.id = id;
    }

    public SignerIdentifier(ASN1OctetString id) {
        this.id = new DERTaggedObject(false, 0, (ASN1Encodable)id);
    }

    public SignerIdentifier(ASN1Primitive id) {
        this.id = id;
    }

    public static SignerIdentifier getInstance(Object o) {
        if (o == null || o instanceof SignerIdentifier) {
            return (SignerIdentifier)((Object)o);
        }
        if (o instanceof IssuerAndSerialNumber) {
            return new SignerIdentifier((IssuerAndSerialNumber)((Object)o));
        }
        if (o instanceof ASN1OctetString) {
            return new SignerIdentifier((ASN1OctetString)o);
        }
        if (o instanceof ASN1Primitive) {
            return new SignerIdentifier((ASN1Primitive)o);
        }
        throw new IllegalArgumentException("Illegal object in SignerIdentifier: " + o.getClass().getName());
    }

    public boolean isTagged() {
        return this.id instanceof ASN1TaggedObject;
    }

    public ASN1Encodable getId() {
        if (this.id instanceof ASN1TaggedObject) {
            return ASN1OctetString.getInstance((ASN1TaggedObject)((ASN1TaggedObject)this.id), (boolean)false);
        }
        return this.id;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.id.toASN1Primitive();
    }
}

