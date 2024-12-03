/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.google.common.base.Preconditions
 */
package com.atlassian.crowd.embedded.core.util;

import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.core.util.CrowdServiceFactory;
import com.google.common.base.Preconditions;

public class StaticCrowdServiceFactory
implements CrowdServiceFactory {
    private static CrowdService crowdService;

    public StaticCrowdServiceFactory(CrowdService crowdService) {
        StaticCrowdServiceFactory.crowdService = (CrowdService)Preconditions.checkNotNull((Object)crowdService);
    }

    public static CrowdService getCrowdService() {
        return crowdService;
    }
}

