/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.settings.SettingsService
 *  com.sun.jersey.spi.container.ContainerRequestFilter
 */
package com.atlassian.confluence.plugins.restapi.filters;

import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.settings.SettingsService;
import com.atlassian.confluence.plugins.restapi.filters.AbstractRequestResourceFilter;
import com.atlassian.confluence.plugins.restapi.filters.ReadOnlyAccessRequestFilter;
import com.sun.jersey.spi.container.ContainerRequestFilter;

public class ReadOnlyAccessResourceFilter
extends AbstractRequestResourceFilter {
    private final AccessModeService accessModeService;
    private final SettingsService settingsService;
    private final boolean hasReadOnlyAccessBlockedAnnotation;

    public ReadOnlyAccessResourceFilter(AccessModeService accessModeService, SettingsService settingsService, boolean hasReadOnlyAccessBlockedAnnotation) {
        this.accessModeService = accessModeService;
        this.settingsService = settingsService;
        this.hasReadOnlyAccessBlockedAnnotation = hasReadOnlyAccessBlockedAnnotation;
    }

    public ContainerRequestFilter getRequestFilter() {
        return new ReadOnlyAccessRequestFilter(this.accessModeService, this.settingsService, this.hasReadOnlyAccessBlockedAnnotation);
    }
}

