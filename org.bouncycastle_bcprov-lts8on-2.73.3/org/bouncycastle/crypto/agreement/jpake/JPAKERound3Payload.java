/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement.jpake;

import java.math.BigInteger;

public class JPAKERound3Payload {
    private final String participantId;
    private final BigInteger macTag;

    public JPAKERound3Payload(String participantId, BigInteger magTag) {
        this.participantId = participantId;
        this.macTag = magTag;
    }

    public String getParticipantId() {
        return this.participantId;
    }

    public BigInteger getMacTag() {
        return this.macTag;
    }
}

