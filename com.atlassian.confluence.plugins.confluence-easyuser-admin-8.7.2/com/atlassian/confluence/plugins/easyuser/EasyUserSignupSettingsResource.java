/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.GlobalSettingsManager
 *  com.atlassian.confluence.user.SignupManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugins.rest.common.security.AuthenticationContext
 *  com.atlassian.user.User
 *  com.google.common.base.CharMatcher
 *  com.google.common.base.Splitter
 *  com.google.common.net.InternetDomainName
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.easyuser;

import com.atlassian.confluence.plugins.easyuser.SignupSettingsBean;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.user.SignupManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugins.rest.common.security.AuthenticationContext;
import com.atlassian.user.User;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.net.InternetDomainName;
import java.security.Principal;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

@Path(value="signup")
public class EasyUserSignupSettingsResource {
    private UserAccessor userAccessor;
    @Context
    protected AuthenticationContext authContext;
    private PermissionManager permissionManager;
    private SignupManager easyUserManager;
    private final GlobalSettingsManager settingsManager;

    public EasyUserSignupSettingsResource(UserAccessor userAccessor, PermissionManager permissionManager, SignupManager easyUserManager, GlobalSettingsManager settingsManager) {
        this.userAccessor = userAccessor;
        this.permissionManager = permissionManager;
        this.easyUserManager = easyUserManager;
        this.settingsManager = settingsManager;
    }

    @POST
    @Path(value="setSignupSettings")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response setSignupSettings(SignupSettingsBean settings) {
        boolean enabled = settings.isEnabled();
        String domains = settings.getDomains();
        boolean notify = settings.isNotifyAdmin();
        if (!this.permissionManager.hasPermission(this.getUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        if (!enabled) {
            if (!StringUtils.isEmpty((CharSequence)domains)) {
                for (String domainString : Splitter.on((CharMatcher)CharMatcher.anyOf((CharSequence)",; ")).omitEmptyStrings().trimResults().split((CharSequence)domains)) {
                    if (InternetDomainName.isValid((String)domainString)) continue;
                    return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"The domains parameter should contain valid internet domains names separated by commas (,)").build();
                }
                this.easyUserManager.setDomainRestrictedSignupMode(domains);
            } else {
                this.easyUserManager.setPrivateSignupMode();
            }
        } else {
            this.easyUserManager.setPublicSignupMode();
        }
        this.easyUserManager.setEmailSentOnInviteSignUp(notify);
        return Response.ok().build();
    }

    public void setAuthContext(AuthenticationContext authContext) {
        this.authContext = authContext;
    }

    private User getUser() {
        Principal principal = this.authContext.getPrincipal();
        if (principal == null) {
            return null;
        }
        if (principal instanceof User) {
            return (User)principal;
        }
        return this.userAccessor.getUserByName(principal.getName());
    }
}

