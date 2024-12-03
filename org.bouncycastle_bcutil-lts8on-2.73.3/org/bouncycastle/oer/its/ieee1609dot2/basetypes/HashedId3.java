/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1OctetString
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId;

public class HashedId3
extends HashedId {
    public HashedId3(byte[] string) {
        super(string);
        if (string.length != 3) {
            throw new IllegalArgumentException("hash id not 3 bytes");
        }
    }

    public static HashedId3 getInstance(Object src) {
        if (src instanceof HashedId3) {
            return (HashedId3)((Object)src);
        }
        if (src != null) {
            byte[] octetString = ASN1OctetString.getInstance((Object)src).getOctets();
            return new HashedId3(octetString);
        }
        return null;
    }
}

