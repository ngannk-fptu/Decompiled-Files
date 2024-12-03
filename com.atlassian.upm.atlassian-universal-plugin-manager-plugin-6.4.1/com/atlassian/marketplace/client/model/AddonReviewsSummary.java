/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.model;

public final class AddonReviewsSummary {
    Float averageStars;
    Integer count;

    public float getAverageStars() {
        return this.averageStars.floatValue();
    }

    public int getCount() {
        return this.count;
    }
}

