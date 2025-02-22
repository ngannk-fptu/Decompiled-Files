/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement.jpake;

import java.math.BigInteger;
import org.bouncycastle.crypto.agreement.jpake.JPAKEUtil;
import org.bouncycastle.util.Arrays;

public class JPAKERound2Payload {
    private final String participantId;
    private final BigInteger a;
    private final BigInteger[] knowledgeProofForX2s;

    public JPAKERound2Payload(String participantId, BigInteger a, BigInteger[] knowledgeProofForX2s) {
        JPAKEUtil.validateNotNull(participantId, "participantId");
        JPAKEUtil.validateNotNull(a, "a");
        JPAKEUtil.validateNotNull(knowledgeProofForX2s, "knowledgeProofForX2s");
        this.participantId = participantId;
        this.a = a;
        this.knowledgeProofForX2s = Arrays.copyOf(knowledgeProofForX2s, knowledgeProofForX2s.length);
    }

    public String getParticipantId() {
        return this.participantId;
    }

    public BigInteger getA() {
        return this.a;
    }

    public BigInteger[] getKnowledgeProofForX2s() {
        return Arrays.copyOf(this.knowledgeProofForX2s, this.knowledgeProofForX2s.length);
    }
}

