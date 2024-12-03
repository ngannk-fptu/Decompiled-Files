/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.extras.api.bamboo;

import com.atlassian.extras.api.LicenseEditionAware;
import com.atlassian.extras.api.ProductLicense;

public interface BambooLicense
extends ProductLicense,
LicenseEditionAware {
    public int getMaximumNumberOfRemoteAgents();

    public int getMaximumNumberOfLocalAgents();

    public int getMaximumNumberOfPlans();

    public boolean isUnlimitedRemoteAgents();

    public boolean isUnlimitedLocalAgents();

    public boolean isUnlimitedPlans();
}

