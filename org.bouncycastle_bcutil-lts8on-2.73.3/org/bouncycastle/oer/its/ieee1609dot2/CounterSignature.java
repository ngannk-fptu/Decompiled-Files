/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Sequence
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Data;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class CounterSignature
extends Ieee1609Dot2Data {
    public CounterSignature(UINT8 protocolVersion, Ieee1609Dot2Content content) {
        super(protocolVersion, content);
    }

    protected CounterSignature(ASN1Sequence instance) {
        super(instance);
    }

    public static Ieee1609Dot2Data getInstance(Object src) {
        if (src instanceof Ieee1609Dot2Data) {
            return (Ieee1609Dot2Data)((Object)src);
        }
        if (src != null) {
            return new CounterSignature(ASN1Sequence.getInstance((Object)src));
        }
        return null;
    }
}

