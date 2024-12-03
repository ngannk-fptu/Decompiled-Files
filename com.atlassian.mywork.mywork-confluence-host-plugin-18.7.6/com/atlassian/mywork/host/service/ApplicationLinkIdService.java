/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.host.spi.HostApplication
 *  com.atlassian.mywork.model.ApplicationLinkIdBuilder
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.mywork.host.service;

import com.atlassian.applinks.host.spi.HostApplication;
import com.atlassian.mywork.model.ApplicationLinkIdBuilder;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ApplicationLinkIdService {
    private final HostApplication hostApplication;

    public ApplicationLinkIdService(@ComponentImport @Qualifier(value="hostApplication") HostApplication hostApplication) {
        this.hostApplication = hostApplication;
    }

    public <T extends ApplicationLinkIdBuilder> T checkAndUpdate(T builder) {
        if (this.hostApplication.getId().get().equals(builder.getApplicationLinkId())) {
            builder.applicationLinkId("");
        }
        return builder;
    }
}

