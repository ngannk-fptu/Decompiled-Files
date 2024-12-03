/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.springframework.beans.factory.annotation.Required
 */
package com.atlassian.confluence.core.actions;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.impl.homepage.HomepageService;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.net.URI;
import org.springframework.beans.factory.annotation.Required;

@RequiresAnyConfluenceAccess
public class IndexAction
extends ConfluenceActionSupport {
    private URI indexUri;
    private HomepageService homepageService;

    @PermittedMethods(value={HttpMethod.GET})
    @XsrfProtectionExcluded
    public String execute() throws Exception {
        this.indexUri = this.homepageService.getHomepage(this.getAuthenticatedUser()).getDeepLinkUri();
        return "forward";
    }

    public String getLocation() {
        return this.indexUri.toString();
    }

    @Required
    public void setHomepageService(HomepageService homepageService) {
        this.homepageService = homepageService;
    }
}

