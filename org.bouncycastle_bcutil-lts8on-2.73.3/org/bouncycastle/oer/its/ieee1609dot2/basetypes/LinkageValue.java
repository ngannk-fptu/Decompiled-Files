/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.DEROctetString
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;

public class LinkageValue
extends DEROctetString {
    public LinkageValue(byte[] string) {
        super(string);
    }

    public LinkageValue(ASN1Encodable obj) throws IOException {
        super(obj);
    }

    public static LinkageValue getInstance(Object src) {
        if (src instanceof LinkageValue) {
            return (LinkageValue)((Object)src);
        }
        if (src != null) {
            return new LinkageValue(ASN1OctetString.getInstance((Object)src).getOctets());
        }
        return null;
    }
}

