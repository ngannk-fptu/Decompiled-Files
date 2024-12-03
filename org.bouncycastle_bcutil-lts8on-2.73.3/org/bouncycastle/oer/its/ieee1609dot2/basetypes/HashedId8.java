/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1OctetString
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId;

public class HashedId8
extends HashedId {
    public HashedId8(byte[] string) {
        super(string);
        if (string.length != 8) {
            throw new IllegalArgumentException("hash id not 8 bytes");
        }
    }

    public static HashedId8 getInstance(Object src) {
        if (src instanceof HashedId8) {
            return (HashedId8)((Object)src);
        }
        if (src != null) {
            byte[] octetString = ASN1OctetString.getInstance((Object)src).getOctets();
            return new HashedId8(octetString);
        }
        return null;
    }
}

