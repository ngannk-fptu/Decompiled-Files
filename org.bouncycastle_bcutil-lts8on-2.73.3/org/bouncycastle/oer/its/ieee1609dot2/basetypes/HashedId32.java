/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1OctetString
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId;

public class HashedId32
extends HashedId {
    public HashedId32(byte[] string) {
        super(string);
        if (string.length != 32) {
            throw new IllegalArgumentException("hash id not 32 bytes");
        }
    }

    public static HashedId32 getInstance(Object src) {
        if (src instanceof HashedId32) {
            return (HashedId32)((Object)src);
        }
        if (src != null) {
            byte[] octetString = ASN1OctetString.getInstance((Object)src).getOctets();
            return new HashedId32(octetString);
        }
        return null;
    }
}

