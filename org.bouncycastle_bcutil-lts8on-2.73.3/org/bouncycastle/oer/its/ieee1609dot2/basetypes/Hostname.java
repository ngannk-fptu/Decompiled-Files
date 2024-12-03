/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1String
 *  org.bouncycastle.asn1.ASN1UTF8String
 *  org.bouncycastle.asn1.DERUTF8String
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERUTF8String;

public class Hostname
extends ASN1Object {
    private final String hostName;

    public Hostname(String hostName) {
        this.hostName = hostName;
    }

    private Hostname(ASN1String string) {
        this.hostName = string.getString();
    }

    public static Hostname getInstance(Object src) {
        if (src instanceof Hostname) {
            return (Hostname)((Object)src);
        }
        if (src != null) {
            return new Hostname((ASN1String)ASN1UTF8String.getInstance((Object)src));
        }
        return null;
    }

    public String getHostName() {
        return this.hostName;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERUTF8String(this.hostName);
    }
}

