/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1OctetString
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

public class PreSharedKeyRecipientInfo
extends HashedId8 {
    public PreSharedKeyRecipientInfo(byte[] string) {
        super(string);
    }

    public static PreSharedKeyRecipientInfo getInstance(Object object) {
        if (object instanceof PreSharedKeyRecipientInfo) {
            return (PreSharedKeyRecipientInfo)((Object)object);
        }
        if (object != null) {
            if (object instanceof HashedId) {
                return new PreSharedKeyRecipientInfo(((HashedId)((Object)object)).getHashBytes());
            }
            return new PreSharedKeyRecipientInfo(ASN1OctetString.getInstance((Object)object).getOctets());
        }
        return null;
    }
}

