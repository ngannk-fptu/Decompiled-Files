/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.directory.spi.ExternalGadgetSpecId
 *  com.atlassian.gadgets.util.AbstractUrlBuilder
 *  com.atlassian.gadgets.util.Uri
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.sal.api.ApplicationProperties
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.directory.internal.impl;

import com.atlassian.gadgets.directory.internal.DirectoryUrlBuilder;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpecId;
import com.atlassian.gadgets.util.AbstractUrlBuilder;
import com.atlassian.gadgets.util.Uri;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.sal.api.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DirectoryUrlBuilderImpl
extends AbstractUrlBuilder
implements DirectoryUrlBuilder {
    @Autowired
    public DirectoryUrlBuilderImpl(@ComponentImport ApplicationProperties applicationProperties, @ComponentImport WebResourceUrlProvider webResourceUrlProvider) {
        super(applicationProperties, webResourceUrlProvider, "com.atlassian.gadgets.dashboard:dashboard-servlet");
    }

    @Override
    public String buildDirectoryResourceUrl() {
        return this.applicationProperties.getBaseUrl() + "/rest/config/1.0/directory";
    }

    @Override
    public String buildDirectoryGadgetResourceUrl(ExternalGadgetSpecId id) {
        return this.buildDirectoryResourceUrl() + "/gadget/" + id;
    }

    @Override
    public String buildSubscribedGadgetFeedsUrl() {
        return this.buildDirectoryResourceUrl() + "/subscribed-gadget-feeds";
    }

    @Override
    public String buildSubscribedGadgetFeedUrl(String feedId) {
        return this.buildSubscribedGadgetFeedsUrl() + "/" + Uri.encodeUriComponent((String)feedId);
    }
}

