/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.preupgrade.model;

import com.atlassian.troubleshooting.preupgrade.model.MicroservicePreUpgradeDataDTO;
import com.atlassian.troubleshooting.preupgrade.model.SupportedPlatformQuery;

public interface SupportedPlatformRules {
    public MicroservicePreUpgradeDataDTO apply(MicroservicePreUpgradeDataDTO var1, SupportedPlatformQuery var2);
}

