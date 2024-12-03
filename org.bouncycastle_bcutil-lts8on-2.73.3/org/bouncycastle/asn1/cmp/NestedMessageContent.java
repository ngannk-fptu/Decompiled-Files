/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Sequence
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.asn1.cmp.PKIMessages;

public class NestedMessageContent
extends PKIMessages {
    public NestedMessageContent(PKIMessage msg) {
        super(msg);
    }

    public NestedMessageContent(PKIMessage[] msgs) {
        super(msgs);
    }

    public NestedMessageContent(ASN1Sequence seq) {
        super(seq);
    }

    public static NestedMessageContent getInstance(Object o) {
        if (o instanceof NestedMessageContent) {
            return (NestedMessageContent)((Object)o);
        }
        if (o != null) {
            return new NestedMessageContent(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }
}

