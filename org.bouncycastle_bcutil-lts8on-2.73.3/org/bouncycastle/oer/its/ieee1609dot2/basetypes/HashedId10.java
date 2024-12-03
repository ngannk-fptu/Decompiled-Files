/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1OctetString
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId;

public class HashedId10
extends HashedId {
    public HashedId10(byte[] string) {
        super(string);
        if (string.length != 10) {
            throw new IllegalArgumentException("hash id not 10 bytes");
        }
    }

    public static HashedId10 getInstance(Object src) {
        if (src instanceof HashedId10) {
            return (HashedId10)((Object)src);
        }
        if (src != null) {
            byte[] octetString = ASN1OctetString.getInstance((Object)src).getOctets();
            return new HashedId10(octetString);
        }
        return null;
    }
}

