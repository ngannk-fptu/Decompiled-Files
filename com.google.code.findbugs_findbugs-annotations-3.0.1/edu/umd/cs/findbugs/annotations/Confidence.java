/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package edu.umd.cs.findbugs.annotations;

import javax.annotation.Nonnull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum Confidence {
    HIGH(1),
    MEDIUM(2),
    LOW(3),
    IGNORE(5);

    private final int confidenceValue;

    @Nonnull
    public static Confidence getConfidence(int prio) {
        for (Confidence c : Confidence.values()) {
            if (prio > c.confidenceValue) continue;
            return c;
        }
        return IGNORE;
    }

    public int getConfidenceValue() {
        return this.confidenceValue;
    }

    private Confidence(int p) {
        this.confidenceValue = p;
    }
}

