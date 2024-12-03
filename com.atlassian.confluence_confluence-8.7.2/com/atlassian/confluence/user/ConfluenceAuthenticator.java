/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.runtime.CommunicationException
 *  com.atlassian.crowd.exception.runtime.OperationFailedException
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.seraph.auth.AuthenticationContextAwareAuthenticator
 *  com.atlassian.seraph.auth.AuthenticationErrorType
 *  com.atlassian.seraph.auth.AuthenticatorException
 *  com.atlassian.seraph.auth.DefaultAuthenticator
 *  com.atlassian.spring.container.ContainerManager
 *  javax.servlet.DispatcherType
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.ldap.PartialResultException
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.event.events.security.LoginDetails;
import com.atlassian.confluence.event.events.security.LoginEvent;
import com.atlassian.confluence.event.events.security.LoginFailedEvent;
import com.atlassian.confluence.security.seraph.ConfluenceAuthenticatorUtils;
import com.atlassian.confluence.security.seraph.ConfluenceUserPrincipal;
import com.atlassian.confluence.setup.SetupContext;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.LoginDetailsHelper;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.crowd.exception.runtime.CommunicationException;
import com.atlassian.crowd.exception.runtime.OperationFailedException;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.seraph.auth.AuthenticationContextAwareAuthenticator;
import com.atlassian.seraph.auth.AuthenticationErrorType;
import com.atlassian.seraph.auth.AuthenticatorException;
import com.atlassian.seraph.auth.DefaultAuthenticator;
import com.atlassian.spring.container.ContainerManager;
import java.security.Principal;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.PartialResultException;

@AuthenticationContextAwareAuthenticator
public class ConfluenceAuthenticator
extends DefaultAuthenticator {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceAuthenticator.class);
    private EventPublisher eventPublisher;
    private UserAccessor userAccessor;

    public boolean logout(HttpServletRequest request, HttpServletResponse response) throws AuthenticatorException {
        AuthenticatedUserThreadLocal.set(null);
        return super.logout(request, response);
    }

    public boolean login(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String username, String password, boolean setRememberMeCookie) throws AuthenticatorException {
        if (username == null || SetupContext.isAvailable()) {
            return false;
        }
        httpServletRequest.setAttribute("com.atlassian.confluence.login.direct", (Object)true);
        boolean result = super.login(httpServletRequest, httpServletResponse, username, password, setRememberMeCookie);
        if (!result) {
            String remoteIP = httpServletRequest.getRemoteAddr();
            String remoteHost = httpServletRequest.getRemoteHost();
            boolean captchaPresent = httpServletRequest.getParameterValues("captchaId") != null;
            this.getEventPublisher().publish((Object)new LoginFailedEvent((Object)this, username, httpServletRequest.getSession().getId(), remoteHost, remoteIP, new LoginDetails(LoginDetails.LoginSource.DIRECT, captchaPresent ? LoginDetails.CaptchaState.PASSED : LoginDetails.CaptchaState.NOT_SHOWN)));
        }
        return result;
    }

    protected boolean authenticate(Principal user, String password) throws AuthenticatorException {
        String username = user.getName();
        try {
            return this.getUserAccessor().authenticate(username, password);
        }
        catch (CommunicationException e) {
            log.warn("CommunicationException caught while authenticating user <" + username + ">", (Throwable)e);
            throw new AuthenticatorException(AuthenticationErrorType.CommunicationError);
        }
        catch (OperationFailedException e) {
            String logWarning = "OperationFailedException caught while authenticating user <" + username + ">. ";
            if (e.getCause() instanceof PartialResultException) {
                logWarning = logWarning + "\n You may need to disable the 'Follow Referrals' option in your LDAP configuration. See http://confluence.atlassian.com/x/0QMMDg";
            }
            log.warn(logWarning, (Throwable)e);
            throw new AuthenticatorException(AuthenticationErrorType.UnknownError);
        }
    }

    protected boolean isPrincipalAlreadyInSessionContext(HttpServletRequest httpServletRequest, Principal principal) {
        return ConfluenceAuthenticatorUtils.isPrincipalAlreadyInSessionContext(httpServletRequest, principal);
    }

    protected void putPrincipalInSessionContext(HttpServletRequest httpServletRequest, Principal principal) {
        super.putPrincipalInSessionContext(httpServletRequest, (Principal)ConfluenceUserPrincipal.of(principal));
    }

    protected Principal getUserFromSession(HttpServletRequest httpServletRequest) {
        try {
            if (httpServletRequest.getSession().getAttribute("seraph_defaultauthenticator_logged_out_user") != null) {
                log.debug("[{}] attribute is present in session", (Object)"seraph_defaultauthenticator_logged_out_user");
                return null;
            }
            Principal principal = (Principal)httpServletRequest.getSession().getAttribute("seraph_defaultauthenticator_user");
            log.debug("Located current user {} in session", (Object)principal);
            return this.refreshPrincipalObtainedFromSession(httpServletRequest, principal);
        }
        catch (RuntimeException e) {
            log.warn("Failed to extract user from session", (Throwable)e);
            return null;
        }
    }

    protected Principal refreshPrincipalObtainedFromSession(HttpServletRequest httpServletRequest, Principal principal) {
        if (httpServletRequest.getDispatcherType() == DispatcherType.ERROR) {
            log.debug("Skipping refresh of [{}] obtained from session for error request", (Object)principal);
            return principal;
        }
        log.debug("Refreshing [{}] obtained from session", (Object)principal);
        return ConfluenceAuthenticatorUtils.refreshPrincipalObtainedFromSession(this.getUserAccessor(), principal);
    }

    protected EventPublisher getEventPublisher() {
        if (this.eventPublisher == null) {
            this.eventPublisher = (EventPublisher)ContainerManager.getInstance().getContainerContext().getComponent((Object)"eventPublisher");
        }
        return this.eventPublisher;
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    protected UserAccessor getUserAccessor() {
        if (this.userAccessor == null) {
            this.userAccessor = (UserAccessor)ContainerManager.getComponent((String)"userAccessor");
        }
        return this.userAccessor;
    }

    protected ConfluenceUser getUser(String uid) {
        return this.getUserAccessor().getUserByName(uid);
    }

    protected boolean authoriseUserAndEstablishSession(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Principal principal) {
        boolean result = super.authoriseUserAndEstablishSession(httpServletRequest, httpServletResponse, principal);
        if (result) {
            String remoteIP = httpServletRequest.getRemoteAddr();
            String remoteHost = httpServletRequest.getRemoteHost();
            LoginDetails.CaptchaState captchaState = httpServletRequest.getParameterValues("captchaId") != null ? LoginDetails.CaptchaState.PASSED : LoginDetails.CaptchaState.NOT_SHOWN;
            LoginDetails loginDetails = new LoginDetails(LoginDetailsHelper.isDirectLogin(httpServletRequest) ? LoginDetails.LoginSource.DIRECT : LoginDetails.LoginSource.COOKIE, captchaState);
            this.getEventPublisher().publish((Object)new LoginEvent((Object)this, principal.getName(), httpServletRequest.getSession().getId(), remoteHost, remoteIP, loginDetails));
        }
        return result;
    }
}

