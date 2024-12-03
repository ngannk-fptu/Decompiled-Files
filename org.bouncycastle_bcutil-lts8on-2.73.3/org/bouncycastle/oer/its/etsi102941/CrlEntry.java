/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1OctetString
 */
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId;

public class CrlEntry
extends HashedId {
    public CrlEntry(byte[] string) {
        super(string);
        if (string.length != 8) {
            throw new IllegalArgumentException("expected 8 bytes");
        }
    }

    private CrlEntry(ASN1OctetString instance) {
        super(instance.getOctets());
    }

    public static CrlEntry getInstance(Object o) {
        if (o instanceof CrlEntry) {
            return (CrlEntry)((Object)o);
        }
        if (o != null) {
            return new CrlEntry(ASN1OctetString.getInstance((Object)o));
        }
        return null;
    }
}

