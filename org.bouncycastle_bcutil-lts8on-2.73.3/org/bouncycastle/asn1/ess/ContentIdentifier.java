/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.DEROctetString
 */
package org.bouncycastle.asn1.ess;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;

public class ContentIdentifier
extends ASN1Object {
    ASN1OctetString value;

    public static ContentIdentifier getInstance(Object o) {
        if (o instanceof ContentIdentifier) {
            return (ContentIdentifier)((Object)o);
        }
        if (o != null) {
            return new ContentIdentifier(ASN1OctetString.getInstance((Object)o));
        }
        return null;
    }

    private ContentIdentifier(ASN1OctetString value) {
        this.value = value;
    }

    public ContentIdentifier(byte[] value) {
        this((ASN1OctetString)new DEROctetString(value));
    }

    public ASN1OctetString getValue() {
        return this.value;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.value;
    }
}

