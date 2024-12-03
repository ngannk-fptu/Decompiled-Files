/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.DEROctetString
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;

public class SubjectAssurance
extends DEROctetString {
    public SubjectAssurance(byte[] string) {
        super(string);
        if (string.length != 1) {
            throw new IllegalArgumentException("length is not 1");
        }
    }

    private SubjectAssurance(ASN1OctetString string) {
        this(string.getOctets());
    }

    public static SubjectAssurance getInstance(Object o) {
        if (o instanceof SubjectAssurance) {
            return (SubjectAssurance)((Object)o);
        }
        if (o != null) {
            return new SubjectAssurance(DEROctetString.getInstance((Object)o));
        }
        return null;
    }
}

