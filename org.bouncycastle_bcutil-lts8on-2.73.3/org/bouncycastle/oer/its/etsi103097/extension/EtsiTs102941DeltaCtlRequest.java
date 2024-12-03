/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Sequence
 */
package org.bouncycastle.oer.its.etsi103097.extension;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.etsi103097.extension.EtsiTs102941CtlRequest;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

public class EtsiTs102941DeltaCtlRequest
extends EtsiTs102941CtlRequest {
    private EtsiTs102941DeltaCtlRequest(ASN1Sequence sequence) {
        super(sequence);
    }

    public EtsiTs102941DeltaCtlRequest(EtsiTs102941CtlRequest request) {
        super(request.getIssuerId(), request.getLastKnownCtlSequence());
    }

    public EtsiTs102941DeltaCtlRequest(HashedId8 issuerId, ASN1Integer lastKnownCtlSequence) {
        super(issuerId, lastKnownCtlSequence);
    }

    public static EtsiTs102941DeltaCtlRequest getInstance(Object o) {
        if (o instanceof EtsiTs102941DeltaCtlRequest) {
            return (EtsiTs102941DeltaCtlRequest)((Object)o);
        }
        if (o != null) {
            return new EtsiTs102941DeltaCtlRequest(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }
}

