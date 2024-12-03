/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.nav.NavigationService
 *  com.atlassian.confluence.rest.api.services.RestNavigation
 *  com.atlassian.confluence.rest.api.services.RestNavigationService
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.rest.navigation.impl;

import com.atlassian.confluence.api.nav.NavigationService;
import com.atlassian.confluence.plugins.rest.navigation.impl.RestNavigationImpl;
import com.atlassian.confluence.rest.api.services.RestNavigation;
import com.atlassian.confluence.rest.api.services.RestNavigationService;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={RestNavigationService.class, NavigationService.class})
public class DefaultRestNavigationService
implements RestNavigationService {
    private final ApplicationProperties applicationProperties;

    @Autowired
    public DefaultRestNavigationService(@ComponentImport ApplicationProperties appProperties) {
        this.applicationProperties = appProperties;
    }

    public RestNavigation createNavigation() {
        return new RestNavigationImpl(this.applicationProperties.getBaseUrl(UrlMode.CANONICAL), this.applicationProperties.getBaseUrl(UrlMode.RELATIVE_CANONICAL));
    }
}

