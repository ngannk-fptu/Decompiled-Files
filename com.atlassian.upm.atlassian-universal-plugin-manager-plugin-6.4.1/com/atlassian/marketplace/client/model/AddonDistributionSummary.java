/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.model;

import io.atlassian.fugue.Option;

public final class AddonDistributionSummary {
    Boolean bundled;
    Boolean bundledCloud;
    Integer downloads;
    Option<Integer> totalInstalls;
    Option<Integer> totalUsers;

    public boolean isBundled() {
        return this.bundled;
    }

    public boolean isBundledCloud() {
        return this.bundledCloud;
    }

    public int getDownloads() {
        return this.downloads;
    }

    public Option<Integer> getTotalInstalls() {
        return this.totalInstalls;
    }

    public Option<Integer> getTotalUsers() {
        return this.totalUsers;
    }
}

