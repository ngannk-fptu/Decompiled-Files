/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.licensebanner.rest;

import com.atlassian.confluence.plugins.licensebanner.support.LicenseBannerService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.sun.jersey.spi.container.ResourceFilters;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Produces(value={"application/json"})
@Consumes(value={"application/json"})
@Path(value="/reminder")
@ResourceFilters(value={SysadminOnlyResourceFilter.class})
public class LicenseReminderResource {
    private static final Logger log = LoggerFactory.getLogger(LicenseReminderResource.class);
    private final UserManager userManager;
    private final LicenseBannerService licenseBannerService;

    public LicenseReminderResource(@ComponentImport UserManager userManager, LicenseBannerService licenseBannerService) {
        this.userManager = userManager;
        this.licenseBannerService = licenseBannerService;
    }

    @POST
    @Path(value="later")
    public void remindMeLater() {
        UserKey remoteUserKey = this.userManager.getRemoteUserKey();
        if (remoteUserKey != null && this.userManager.isSystemAdmin(remoteUserKey)) {
            this.licenseBannerService.remindLater(remoteUserKey);
        }
    }

    @POST
    @Path(value="never")
    public void remindMeNever() {
        UserKey remoteUserKey = this.userManager.getRemoteUserKey();
        if (remoteUserKey != null && this.userManager.isSystemAdmin(remoteUserKey)) {
            this.licenseBannerService.remindNever(remoteUserKey);
        }
    }
}

