/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade;

public interface UpgradeGate {
    public void setUpgradeRequired(boolean var1);

    public void setPluginDependentUpgradeComplete(boolean var1);

    public boolean isUpgradeRequiredWithWait();

    public boolean isPluginDependentUpgradeCompleteWithWait();
}

