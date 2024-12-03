/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.pats.access.services;

import com.atlassian.pats.access.services.ReadOnlyModeService;

public final class JiraReadOnlyModeService
implements ReadOnlyModeService {
    @Override
    public boolean isEnabled() {
        return false;
    }
}

