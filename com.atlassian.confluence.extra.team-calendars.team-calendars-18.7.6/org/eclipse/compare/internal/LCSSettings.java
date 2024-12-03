/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.compare.internal;

public class LCSSettings {
    private double tooLong = 1.0E7;
    private double powLimit = 1.5;
    private boolean useGreedyMethod = false;

    public double getTooLong() {
        return this.tooLong;
    }

    public void setTooLong(double too_long) {
        this.tooLong = too_long;
    }

    public double getPowLimit() {
        return this.powLimit;
    }

    public void setPowLimit(double pow_limit) {
        this.powLimit = pow_limit;
    }

    public boolean isUseGreedyMethod() {
        return this.useGreedyMethod;
    }

    public void setUseGreedyMethod(boolean useGreedyMethod) {
        this.useGreedyMethod = useGreedyMethod;
    }
}

