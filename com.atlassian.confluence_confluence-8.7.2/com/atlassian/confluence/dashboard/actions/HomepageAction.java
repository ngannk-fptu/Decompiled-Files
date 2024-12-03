/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.dashboard.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.impl.homepage.Homepage;
import com.atlassian.confluence.impl.homepage.HomepageService;
import com.atlassian.confluence.security.access.annotations.RequiresLicensedOrAnonymousConfluenceAccess;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.net.URI;

@Deprecated
@RequiresLicensedOrAnonymousConfluenceAccess
public class HomepageAction
extends ConfluenceActionSupport {
    private URI redirectLocation;
    private HomepageService homepageService;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        Homepage homepage = this.homepageService.getHomepage(this.getAuthenticatedUser());
        this.redirectLocation = homepage.getDeepLinkUri();
        return "success";
    }

    public String getRedirectLocation() {
        return this.redirectLocation.toString();
    }

    public void setHomepageService(HomepageService homepageService) {
        this.homepageService = homepageService;
    }
}

