/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.user.SendUserInviteEvent
 *  com.atlassian.confluence.user.SignupManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.user.notifications.NotificationSendResult
 *  com.atlassian.plugins.rest.common.security.AuthenticationContext
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.atlassian.user.User
 *  com.google.errorprone.annotations.Immutable
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.easyuser;

import com.atlassian.confluence.event.events.user.SendUserInviteEvent;
import com.atlassian.confluence.plugins.easyuser.UserInvites;
import com.atlassian.confluence.user.SignupManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.notifications.NotificationSendResult;
import com.atlassian.plugins.rest.common.security.AuthenticationContext;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.atlassian.user.User;
import com.google.errorprone.annotations.Immutable;
import com.sun.jersey.spi.container.ResourceFilters;
import java.security.Principal;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Path(value="/")
@ResourceFilters(value={AdminOnlyResourceFilter.class})
public class EasyUserResource {
    private UserAccessor userAccessor;
    @Context
    protected AuthenticationContext authContext;
    private final SignupManager easyUserManager;

    public EasyUserResource(UserAccessor userAccessor, SignupManager easyUserManager) {
        this.userAccessor = userAccessor;
        this.easyUserManager = easyUserManager;
    }

    @POST
    @Path(value="refreshToken")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response newInviteUrl() {
        this.easyUserManager.refreshAndGetToken();
        return this.okResponseWithSignupUrl();
    }

    @POST
    @Path(value="sendUserInvites")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response sendUserInvites(UserInvites userInvites) {
        SendUserInviteEvent event = userInvites.buildEvent(this, this.getUser());
        NotificationSendResult result = this.easyUserManager.sendInvites(event);
        return Response.ok((Object)result).build();
    }

    @POST
    @Path(value="undoTokenReset")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response undoTokenReset() {
        this.easyUserManager.restorePreviousToken();
        return this.okResponseWithSignupUrl();
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

    private Response okResponseWithSignupUrl() {
        return Response.ok((Object)new Result(this.easyUserManager.getSignupURL(), this.easyUserManager.isEmailSentOnInviteSignUp())).build();
    }

    public void setAuthContext(AuthenticationContext authContext) {
        this.authContext = authContext;
    }

    @XmlRootElement
    @Immutable
    static class Result {
        @XmlElement
        private final String signupUrl;
        @XmlElement
        private final boolean notifyAdmin;

        public Result() {
            this.signupUrl = null;
            this.notifyAdmin = false;
        }

        public Result(String signupUrl, boolean notifyAdmin) {
            this.signupUrl = signupUrl;
            this.notifyAdmin = notifyAdmin;
        }

        public String getSignupUrl() {
            return this.signupUrl;
        }

        public boolean isNotifyAdmin() {
            return this.notifyAdmin;
        }
    }
}

