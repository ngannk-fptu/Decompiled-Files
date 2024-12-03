/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade;

public interface UpgradeTaskInfo {
    public String getBuildNumber();

    public String getName();

    public String getShortDescription();

    public boolean isDatabaseUpgrade();
}

