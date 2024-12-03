/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Sequence
 */
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataSignedAndEncryptedUnicast;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;

public class EnrolmentRequestMessage
extends EtsiTs103097DataSignedAndEncryptedUnicast {
    public EnrolmentRequestMessage(Ieee1609Dot2Content content) {
        super(content);
    }

    protected EnrolmentRequestMessage(ASN1Sequence src) {
        super(src);
    }

    public static EnrolmentRequestMessage getInstance(Object o) {
        if (o instanceof EnrolmentRequestMessage) {
            return (EnrolmentRequestMessage)((Object)o);
        }
        if (o != null) {
            return new EnrolmentRequestMessage(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }
}

