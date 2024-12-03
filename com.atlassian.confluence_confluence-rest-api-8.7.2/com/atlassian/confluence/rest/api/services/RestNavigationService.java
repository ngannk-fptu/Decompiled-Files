/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.nav.NavigationService
 */
package com.atlassian.confluence.rest.api.services;

import com.atlassian.confluence.api.nav.NavigationService;
import com.atlassian.confluence.rest.api.services.RestNavigation;

public interface RestNavigationService
extends NavigationService {
    public static final String BASE_PATH = "/rest/api";

    public RestNavigation createNavigation();
}

