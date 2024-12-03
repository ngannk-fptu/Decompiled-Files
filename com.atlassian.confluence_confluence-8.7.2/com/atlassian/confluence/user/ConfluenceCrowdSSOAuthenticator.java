/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.integration.http.CrowdHttpAuthenticator
 *  com.atlassian.crowd.integration.rest.service.factory.RestCrowdHttpAuthenticationFactory
 *  com.atlassian.crowd.integration.seraph.CrowdAuthenticator
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.seraph.auth.AuthenticationContextAwareAuthenticator
 *  com.atlassian.seraph.auth.AuthenticatorException
 *  com.atlassian.seraph.config.SecurityConfig
 *  com.atlassian.seraph.service.rememberme.RememberMeService
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.annotations.VisibleForTesting
 *  io.atlassian.fugue.Suppliers
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.event.events.security.LoginDetails;
import com.atlassian.confluence.event.events.security.LoginEvent;
import com.atlassian.confluence.event.events.security.LoginFailedEvent;
import com.atlassian.confluence.impl.security.recovery.RecoveryUtil;
import com.atlassian.confluence.impl.seraph.AuthenticatorMetrics;
import com.atlassian.confluence.security.seraph.ConfluenceAuthenticatorUtils;
import com.atlassian.confluence.security.seraph.ConfluenceUserPrincipal;
import com.atlassian.confluence.user.ConfluenceAuthenticator;
import com.atlassian.confluence.user.LoginDetailsHelper;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.MemoizingComponentReference;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.integration.http.CrowdHttpAuthenticator;
import com.atlassian.crowd.integration.rest.service.factory.RestCrowdHttpAuthenticationFactory;
import com.atlassian.crowd.integration.seraph.CrowdAuthenticator;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.seraph.auth.AuthenticationContextAwareAuthenticator;
import com.atlassian.seraph.auth.AuthenticatorException;
import com.atlassian.seraph.config.SecurityConfig;
import com.atlassian.seraph.service.rememberme.RememberMeService;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.annotations.VisibleForTesting;
import io.atlassian.fugue.Suppliers;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@AuthenticationContextAwareAuthenticator
public class ConfluenceCrowdSSOAuthenticator
extends CrowdAuthenticator {
    private final Supplier<EventPublisher> eventPublisherRef;
    private final Supplier<UserAccessor> userAccessorRef;
    private final Supplier<RememberMeService> rememberMeServiceRef;
    private final ConfluenceAuthenticator simpleAuthenticator;
    private final Supplier<CrowdDirectoryService> crowdDirectoryServiceRef;

    public ConfluenceCrowdSSOAuthenticator() {
        this(RestCrowdHttpAuthenticationFactory.getAuthenticator(), ConfluenceCrowdSSOAuthenticator.componentSupplier("userAccessor"), ConfluenceCrowdSSOAuthenticator.componentSupplier("eventPublisher"), ConfluenceCrowdSSOAuthenticator.componentSupplier("crowdService"), ConfluenceCrowdSSOAuthenticator.componentSupplier("crowdDirectoryService"), Optional.empty(), new ConfluenceAuthenticator());
    }

    @VisibleForTesting
    ConfluenceCrowdSSOAuthenticator(CrowdHttpAuthenticator crowdHttpAuthenticator, Supplier<UserAccessor> userAccessorRef, Supplier<EventPublisher> eventPublisherRef, Supplier<CrowdService> crowdServiceRef, Supplier<CrowdDirectoryService> crowdDirectoryServiceRef, Optional<RememberMeService> rememberMeServiceRef, ConfluenceAuthenticator simpleAuthenticator) {
        super(crowdHttpAuthenticator, crowdServiceRef);
        this.userAccessorRef = userAccessorRef;
        this.eventPublisherRef = eventPublisherRef;
        this.crowdDirectoryServiceRef = crowdDirectoryServiceRef;
        Supplier<RememberMeService> defaultRememberMeServiceRef = () -> super.getRememberMeService();
        this.rememberMeServiceRef = () -> (RememberMeService)rememberMeServiceRef.orElseGet(defaultRememberMeServiceRef);
        this.simpleAuthenticator = simpleAuthenticator;
    }

    public void init(Map<String, String> params, SecurityConfig config) {
        super.init(params, config);
        this.simpleAuthenticator.init(params, config);
    }

    public Principal getUser(HttpServletRequest request, HttpServletResponse response) {
        Principal user;
        Supplier userRef = Suppliers.memoize(() -> this.simpleAuthenticator.getUser(request, response));
        if (RecoveryUtil.isRecoveryMode() && (user = (Principal)userRef.get()) != null && RecoveryUtil.isRecoveryAdmin(user.getName())) {
            return user;
        }
        if (!this.isSSOEnabled()) {
            return (Principal)userRef.get();
        }
        return AuthenticatorMetrics.measureGetUser(() -> super.getUser(request, response));
    }

    protected void logoutUser(HttpServletRequest request) {
    }

    protected Principal getUser(String username) {
        return this.users().getUserByName(username);
    }

    public boolean login(HttpServletRequest request, HttpServletResponse response, String username, String password, boolean cookie) throws AuthenticatorException {
        if (!this.isSSOEnabled() || RecoveryUtil.isRecoveryAdmin(username)) {
            return this.simpleAuthenticator.login(request, response, username, password, cookie);
        }
        request.setAttribute("com.atlassian.confluence.login.direct", (Object)true);
        boolean success = super.login(request, response, username, password, cookie);
        if (!success) {
            this.fireLoginEvent(request, username, false);
        }
        return success;
    }

    private void fireLoginEvent(HttpServletRequest request, String username, boolean success) {
        String remoteIP = request.getRemoteAddr();
        String remoteHost = request.getRemoteHost();
        String sessionId = request.getSession().getId();
        LoginDetails.CaptchaState captchaState = request.getParameterValues("captchaId") != null ? LoginDetails.CaptchaState.PASSED : LoginDetails.CaptchaState.NOT_SHOWN;
        LoginDetails loginDetails = new LoginDetails(LoginDetailsHelper.isDirectLogin(request) ? LoginDetails.LoginSource.DIRECT : LoginDetails.LoginSource.COOKIE, captchaState);
        this.events().publish((Object)(success ? new LoginEvent((Object)this, username, sessionId, remoteHost, remoteIP, loginDetails) : new LoginFailedEvent((Object)this, username, sessionId, remoteHost, remoteIP, loginDetails)));
    }

    private EventPublisher events() {
        return this.eventPublisherRef.get();
    }

    private UserAccessor users() {
        return this.userAccessorRef.get();
    }

    protected RememberMeService getRememberMeService() {
        return this.rememberMeServiceRef.get();
    }

    protected boolean authoriseUserAndEstablishSession(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Principal principal) {
        boolean success = super.authoriseUserAndEstablishSession(httpServletRequest, httpServletResponse, principal);
        if (success && !LoginDetailsHelper.isSsoLogin(httpServletRequest)) {
            this.fireLoginEvent(httpServletRequest, principal.getName(), true);
        }
        return success;
    }

    protected boolean isPrincipalAlreadyInSessionContext(HttpServletRequest httpServletRequest, Principal principal) {
        return ConfluenceAuthenticatorUtils.isPrincipalAlreadyInSessionContext(httpServletRequest, principal);
    }

    protected void putPrincipalInSessionContext(HttpServletRequest httpServletRequest, Principal principal) {
        super.putPrincipalInSessionContext(httpServletRequest, (Principal)ConfluenceUserPrincipal.of(principal));
    }

    protected Principal refreshPrincipalObtainedFromSession(HttpServletRequest httpServletRequest, Principal principal) {
        return ConfluenceAuthenticatorUtils.refreshPrincipalObtainedFromSession(this.users(), principal);
    }

    private static <T> Supplier<T> componentSupplier(String componentId) {
        return MemoizingComponentReference.containerComponent(componentId);
    }

    @VisibleForTesting
    public boolean isCrowdSetup() {
        if (ContainerManager.isContainerSetup()) {
            CrowdDirectoryService service = this.crowdDirectoryServiceRef.get();
            return service.findAllDirectories().stream().anyMatch(directory -> directory.isActive() && directory.getType().equals((Object)DirectoryType.CROWD));
        }
        return false;
    }

    private boolean isSSOEnabled() {
        return this.isCrowdSetup();
    }
}

