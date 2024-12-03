/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.api;

public enum PasswordScore {
    UNKNOWN(-1),
    WEAK(0),
    FAIR(1),
    GOOD(2),
    STRONG(3),
    VERY_STRONG(4);

    private final int ranking;

    public static PasswordScore fromRanking(long ranking) {
        for (PasswordScore score : PasswordScore.values()) {
            if ((long)score.ranking != ranking) continue;
            return score;
        }
        throw new IllegalArgumentException("Ranking " + ranking + " does not correspond to valid password score's ranking");
    }

    private PasswordScore(int ranking) {
        this.ranking = ranking;
    }

    public boolean isAtLeast(PasswordScore minimumScore) {
        return this.ranking >= minimumScore.ranking;
    }

    public long getRanking() {
        return this.ranking;
    }
}

