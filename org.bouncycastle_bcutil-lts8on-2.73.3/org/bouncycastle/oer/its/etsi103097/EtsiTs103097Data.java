/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Sequence
 */
package org.bouncycastle.oer.its.etsi103097;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Data;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class EtsiTs103097Data
extends Ieee1609Dot2Data {
    public EtsiTs103097Data(Ieee1609Dot2Content content) {
        super(new UINT8(3), content);
    }

    public EtsiTs103097Data(UINT8 protocolVersion, Ieee1609Dot2Content content) {
        super(protocolVersion, content);
    }

    protected EtsiTs103097Data(ASN1Sequence src) {
        super(src);
    }

    public static EtsiTs103097Data getInstance(Object o) {
        if (o instanceof EtsiTs103097Data) {
            return (EtsiTs103097Data)((Object)o);
        }
        if (o != null) {
            return new EtsiTs103097Data(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }
}

