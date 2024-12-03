/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.properties.appenders;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.troubleshooting.stp.spi.RootLevelSupportDataAppender;
import com.atlassian.troubleshooting.stp.spi.SupportDataBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class ApplicationLinksDataAppender
extends RootLevelSupportDataAppender {
    private final ApplicationLinkService linkService;

    @Autowired
    public ApplicationLinksDataAppender(ApplicationLinkService linkService) {
        this.linkService = linkService;
    }

    @Override
    protected void addSupportData(SupportDataBuilder builder) {
        this.addApplicationLinkInformation(builder.addCategory("stp.properties.links"));
    }

    private void addApplicationLinkInformation(SupportDataBuilder builder) {
        for (ApplicationLink link : this.linkService.getApplicationLinks()) {
            SupportDataBuilder linkBuilder = builder.addCategory("stp.properties.links");
            linkBuilder.addValue("stp.properties.links.id", link.getId().toString());
            linkBuilder.addValue("stp.properties.links.name", link.getName());
            linkBuilder.addValue("stp.properties.links.primary", String.valueOf(link.isPrimary()));
            linkBuilder.addValue("stp.properties.links.type", link.getType().getI18nKey());
            linkBuilder.addValue("stp.properties.links.display.url", link.getDisplayUrl().toString());
            linkBuilder.addValue("stp.properties.links.rpc.url", link.getRpcUrl().toString());
            linkBuilder.addContext(link);
        }
    }
}

