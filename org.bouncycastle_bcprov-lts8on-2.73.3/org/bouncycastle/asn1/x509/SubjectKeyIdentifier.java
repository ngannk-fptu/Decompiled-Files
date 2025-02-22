/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.util.Arrays;

public class SubjectKeyIdentifier
extends ASN1Object {
    private byte[] keyidentifier;

    public static SubjectKeyIdentifier getInstance(ASN1TaggedObject obj, boolean explicit) {
        return SubjectKeyIdentifier.getInstance(ASN1OctetString.getInstance(obj, explicit));
    }

    public static SubjectKeyIdentifier getInstance(Object obj) {
        if (obj instanceof SubjectKeyIdentifier) {
            return (SubjectKeyIdentifier)obj;
        }
        if (obj != null) {
            return new SubjectKeyIdentifier(ASN1OctetString.getInstance(obj));
        }
        return null;
    }

    public static SubjectKeyIdentifier fromExtensions(Extensions extensions) {
        return SubjectKeyIdentifier.getInstance(Extensions.getExtensionParsedValue(extensions, Extension.subjectKeyIdentifier));
    }

    public SubjectKeyIdentifier(byte[] keyid) {
        this.keyidentifier = Arrays.clone(keyid);
    }

    protected SubjectKeyIdentifier(ASN1OctetString keyid) {
        this(keyid.getOctets());
    }

    public byte[] getKeyIdentifier() {
        return Arrays.clone(this.keyidentifier);
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DEROctetString(this.getKeyIdentifier());
    }
}

