/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Boolean
 *  org.bouncycastle.asn1.ASN1Sequence
 */
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.etsi102941.CtlFormat;
import org.bouncycastle.oer.its.etsi102941.SequenceOfCtlCommand;
import org.bouncycastle.oer.its.etsi102941.basetypes.Version;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class ToBeSignedTlmCtl
extends CtlFormat {
    public ToBeSignedTlmCtl(Version version, Time32 nextUpdate, ASN1Boolean isFullCtl, UINT8 ctlSequence, SequenceOfCtlCommand ctlCommands) {
        super(version, nextUpdate, isFullCtl, ctlSequence, ctlCommands);
    }

    public ToBeSignedTlmCtl(ASN1Sequence seq) {
        super(seq);
    }

    public static ToBeSignedTlmCtl getInstance(Object o) {
        if (o instanceof ToBeSignedTlmCtl) {
            return (ToBeSignedTlmCtl)((Object)o);
        }
        if (o != null) {
            return new ToBeSignedTlmCtl(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }
}

