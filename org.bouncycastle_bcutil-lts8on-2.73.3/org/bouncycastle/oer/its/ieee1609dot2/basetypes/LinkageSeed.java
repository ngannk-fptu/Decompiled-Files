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

public class LinkageSeed
extends ASN1Object {
    private final byte[] linkageSeed;

    public LinkageSeed(byte[] linkageSeed) {
        if (linkageSeed.length != 16) {
            throw new IllegalArgumentException("linkage seed not 16 bytes");
        }
        this.linkageSeed = Arrays.clone((byte[])linkageSeed);
    }

    private LinkageSeed(ASN1OctetString value) {
        this(value.getOctets());
    }

    public static LinkageSeed getInstance(Object o) {
        if (o instanceof LinkageSeed) {
            return (LinkageSeed)((Object)o);
        }
        if (o != null) {
            return new LinkageSeed(DEROctetString.getInstance((Object)o));
        }
        return null;
    }

    public byte[] getLinkageSeed() {
        return this.linkageSeed;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DEROctetString(this.linkageSeed);
    }
}

