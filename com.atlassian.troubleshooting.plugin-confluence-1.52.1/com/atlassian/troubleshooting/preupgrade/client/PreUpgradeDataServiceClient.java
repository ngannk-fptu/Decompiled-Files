/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.preupgrade.client;

import com.atlassian.troubleshooting.preupgrade.model.SupportedPlatformQuery;
import java.util.Optional;

public interface PreUpgradeDataServiceClient {
    public Optional<String> findSupportedPlatformInfoJsonForQuery(SupportedPlatformQuery var1);
}

