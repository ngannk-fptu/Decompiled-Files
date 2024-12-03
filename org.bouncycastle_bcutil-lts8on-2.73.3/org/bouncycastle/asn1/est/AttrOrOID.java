/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.pkcs.Attribute
 */
package org.bouncycastle.asn1.est;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.Attribute;

public class AttrOrOID
extends ASN1Object
implements ASN1Choice {
    private final ASN1ObjectIdentifier oid;
    private final Attribute attribute;

    public AttrOrOID(ASN1ObjectIdentifier oid) {
        this.oid = oid;
        this.attribute = null;
    }

    public AttrOrOID(Attribute attribute) {
        this.oid = null;
        this.attribute = attribute;
    }

    public static AttrOrOID getInstance(Object obj) {
        if (obj instanceof AttrOrOID) {
            return (AttrOrOID)((Object)obj);
        }
        if (obj != null) {
            if (obj instanceof ASN1Encodable) {
                ASN1Primitive asn1Prim = ((ASN1Encodable)obj).toASN1Primitive();
                if (asn1Prim instanceof ASN1ObjectIdentifier) {
                    return new AttrOrOID(ASN1ObjectIdentifier.getInstance((Object)asn1Prim));
                }
                if (asn1Prim instanceof ASN1Sequence) {
                    return new AttrOrOID(Attribute.getInstance((Object)asn1Prim));
                }
            }
            if (obj instanceof byte[]) {
                try {
                    return AttrOrOID.getInstance(ASN1Primitive.fromByteArray((byte[])((byte[])obj)));
                }
                catch (IOException e) {
                    throw new IllegalArgumentException("unknown encoding in getInstance()");
                }
            }
            throw new IllegalArgumentException("unknown object in getInstance(): " + obj.getClass().getName());
        }
        return null;
    }

    public boolean isOid() {
        return this.oid != null;
    }

    public ASN1ObjectIdentifier getOid() {
        return this.oid;
    }

    public Attribute getAttribute() {
        return this.attribute;
    }

    public ASN1Primitive toASN1Primitive() {
        if (this.oid != null) {
            return this.oid;
        }
        return this.attribute.toASN1Primitive();
    }
}

