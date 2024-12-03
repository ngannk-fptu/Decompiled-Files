/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.analytics.client.base;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="analytics.stash.base.data")
public class BitbucketBaseDataEvent {
    private final int userCount;
    private final int projectCount;
    private final int repositoryCount;

    public BitbucketBaseDataEvent(int userCount, int projectCount, int repositoryCount) {
        this.userCount = userCount;
        this.projectCount = projectCount;
        this.repositoryCount = repositoryCount;
    }

    public int getUserCount() {
        return this.userCount;
    }

    public int getProjectCount() {
        return this.projectCount;
    }

    public int getRepositoryCount() {
        return this.repositoryCount;
    }
}

