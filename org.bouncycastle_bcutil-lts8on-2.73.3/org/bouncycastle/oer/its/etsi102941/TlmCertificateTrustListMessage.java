/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Sequence
 */
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataSigned;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;

public class TlmCertificateTrustListMessage
extends EtsiTs103097DataSigned {
    public TlmCertificateTrustListMessage(Ieee1609Dot2Content content) {
        super(content);
    }

    protected TlmCertificateTrustListMessage(ASN1Sequence src) {
        super(src);
    }

    public static TlmCertificateTrustListMessage getInstance(Object o) {
        if (o instanceof TlmCertificateTrustListMessage) {
            return (TlmCertificateTrustListMessage)((Object)o);
        }
        if (o != null) {
            return new TlmCertificateTrustListMessage(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }
}

