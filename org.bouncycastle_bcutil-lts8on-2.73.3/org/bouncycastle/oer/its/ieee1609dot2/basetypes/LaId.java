/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;

public class LaId
extends ASN1Object {
    private final byte[] laId;

    public LaId(byte[] laId) {
        this.laId = laId;
        this.assertLength();
    }

    private LaId(ASN1OctetString octetString) {
        this(octetString.getOctets());
    }

    public static LaId getInstance(Object o) {
        if (o instanceof LaId) {
            return (LaId)((Object)o);
        }
        if (o != null) {
            return new LaId(DEROctetString.getInstance((Object)o));
        }
        return null;
    }

    private void assertLength() {
        if (this.laId.length != 2) {
            throw new IllegalArgumentException("laId must be 2 octets");
        }
    }

    public byte[] getLaId() {
        return Arrays.clone((byte[])this.laId);
    }

    public ASN1Primitive toASN1Primitive() {
        return new DEROctetString(this.laId);
    }
}

