/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 */
package com.atlassian.pats.access.services;

import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.pats.access.services.ReadOnlyModeService;

public final class ConfluenceReadOnlyModeService
implements ReadOnlyModeService {
    private final AccessModeService accessModeService;

    public ConfluenceReadOnlyModeService(AccessModeService accessModeService) {
        this.accessModeService = accessModeService;
    }

    @Override
    public boolean isEnabled() {
        return this.accessModeService.isReadOnlyAccessModeEnabled();
    }
}

