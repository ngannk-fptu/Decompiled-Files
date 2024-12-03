/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.seraph.ioc;

import com.atlassian.seraph.service.rememberme.NoopRememberMeService;
import com.atlassian.seraph.service.rememberme.RememberMeService;

public class ApplicationServicesRegistry {
    private static volatile RememberMeService rememberMeService = NoopRememberMeService.INSTANCE;

    public static void setRememberMeService(RememberMeService rememberMeService) {
        if (rememberMeService == null) {
            throw new IllegalArgumentException("rememberMeService must not be null.");
        }
        ApplicationServicesRegistry.rememberMeService = rememberMeService;
    }

    public static RememberMeService getRememberMeService() {
        return rememberMeService;
    }
}

