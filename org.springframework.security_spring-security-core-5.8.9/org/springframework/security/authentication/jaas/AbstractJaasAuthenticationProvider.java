/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.context.ApplicationEventPublisher
 *  org.springframework.context.ApplicationEventPublisherAware
 *  org.springframework.context.ApplicationListener
 *  org.springframework.core.log.LogMessage
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.security.authentication.jaas;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.core.log.LogMessage;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.jaas.AuthorityGranter;
import org.springframework.security.authentication.jaas.DefaultLoginExceptionResolver;
import org.springframework.security.authentication.jaas.JaasAuthenticationCallbackHandler;
import org.springframework.security.authentication.jaas.JaasAuthenticationToken;
import org.springframework.security.authentication.jaas.JaasGrantedAuthority;
import org.springframework.security.authentication.jaas.JaasNameCallbackHandler;
import org.springframework.security.authentication.jaas.JaasPasswordCallbackHandler;
import org.springframework.security.authentication.jaas.LoginExceptionResolver;
import org.springframework.security.authentication.jaas.event.JaasAuthenticationFailedEvent;
import org.springframework.security.authentication.jaas.event.JaasAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

public abstract class AbstractJaasAuthenticationProvider
implements AuthenticationProvider,
ApplicationEventPublisherAware,
InitializingBean,
ApplicationListener<SessionDestroyedEvent> {
    private ApplicationEventPublisher applicationEventPublisher;
    private AuthorityGranter[] authorityGranters;
    private JaasAuthenticationCallbackHandler[] callbackHandlers;
    protected final Log log = LogFactory.getLog(this.getClass());
    private LoginExceptionResolver loginExceptionResolver = new DefaultLoginExceptionResolver();
    private String loginContextName = "SPRINGSECURITY";

    public void afterPropertiesSet() throws Exception {
        Assert.hasLength((String)this.loginContextName, (String)"loginContextName cannot be null or empty");
        Assert.notEmpty((Object[])this.authorityGranters, (String)"authorityGranters cannot be null or empty");
        if (ObjectUtils.isEmpty((Object[])this.callbackHandlers)) {
            this.setCallbackHandlers(new JaasAuthenticationCallbackHandler[]{new JaasNameCallbackHandler(), new JaasPasswordCallbackHandler()});
        }
        Assert.notNull((Object)this.loginExceptionResolver, (String)"loginExceptionResolver cannot be null");
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        if (!(auth instanceof UsernamePasswordAuthenticationToken)) {
            return null;
        }
        UsernamePasswordAuthenticationToken request = (UsernamePasswordAuthenticationToken)auth;
        try {
            LoginContext loginContext = this.createLoginContext(new InternalCallbackHandler(auth));
            loginContext.login();
            Set<Principal> principals = loginContext.getSubject().getPrincipals();
            Set<GrantedAuthority> authorities = this.getAuthorities(principals);
            JaasAuthenticationToken result = new JaasAuthenticationToken(request.getPrincipal(), request.getCredentials(), new ArrayList<GrantedAuthority>(authorities), loginContext);
            this.publishSuccessEvent(result);
            return result;
        }
        catch (LoginException ex) {
            AuthenticationException resolvedException = this.loginExceptionResolver.resolveException(ex);
            this.publishFailureEvent(request, resolvedException);
            throw resolvedException;
        }
    }

    private Set<GrantedAuthority> getAuthorities(Set<Principal> principals) {
        HashSet<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        for (Principal principal : principals) {
            for (AuthorityGranter granter : this.authorityGranters) {
                Set<String> roles = granter.grant(principal);
                if (CollectionUtils.isEmpty(roles)) continue;
                for (String role : roles) {
                    authorities.add(new JaasGrantedAuthority(role, principal));
                }
            }
        }
        return authorities;
    }

    protected abstract LoginContext createLoginContext(CallbackHandler var1) throws LoginException;

    protected void handleLogout(SessionDestroyedEvent event) {
        List<SecurityContext> contexts = event.getSecurityContexts();
        if (contexts.isEmpty()) {
            this.log.debug((Object)"The destroyed session has no SecurityContexts");
            return;
        }
        for (SecurityContext context : contexts) {
            Authentication auth = context.getAuthentication();
            if (auth == null || !(auth instanceof JaasAuthenticationToken)) continue;
            JaasAuthenticationToken token = (JaasAuthenticationToken)auth;
            try {
                LoginContext loginContext = token.getLoginContext();
                this.logout(token, loginContext);
            }
            catch (LoginException ex) {
                this.log.warn((Object)"Error error logging out of LoginContext", (Throwable)ex);
            }
        }
    }

    private void logout(JaasAuthenticationToken token, LoginContext loginContext) throws LoginException {
        if (loginContext != null) {
            this.log.debug((Object)LogMessage.of(() -> "Logging principal: [" + token.getPrincipal() + "] out of LoginContext"));
            loginContext.logout();
            return;
        }
        this.log.debug((Object)LogMessage.of(() -> "Cannot logout principal: [" + token.getPrincipal() + "] from LoginContext. The LoginContext is unavailable"));
    }

    public void onApplicationEvent(SessionDestroyedEvent event) {
        this.handleLogout(event);
    }

    protected void publishFailureEvent(UsernamePasswordAuthenticationToken token, AuthenticationException ase) {
        if (this.applicationEventPublisher != null) {
            this.applicationEventPublisher.publishEvent((ApplicationEvent)new JaasAuthenticationFailedEvent(token, ase));
        }
    }

    protected void publishSuccessEvent(UsernamePasswordAuthenticationToken token) {
        if (this.applicationEventPublisher != null) {
            this.applicationEventPublisher.publishEvent((ApplicationEvent)new JaasAuthenticationSuccessEvent(token));
        }
    }

    AuthorityGranter[] getAuthorityGranters() {
        return this.authorityGranters;
    }

    public void setAuthorityGranters(AuthorityGranter[] authorityGranters) {
        this.authorityGranters = authorityGranters;
    }

    JaasAuthenticationCallbackHandler[] getCallbackHandlers() {
        return this.callbackHandlers;
    }

    public void setCallbackHandlers(JaasAuthenticationCallbackHandler[] callbackHandlers) {
        this.callbackHandlers = callbackHandlers;
    }

    String getLoginContextName() {
        return this.loginContextName;
    }

    public void setLoginContextName(String loginContextName) {
        this.loginContextName = loginContextName;
    }

    LoginExceptionResolver getLoginExceptionResolver() {
        return this.loginExceptionResolver;
    }

    public void setLoginExceptionResolver(LoginExceptionResolver loginExceptionResolver) {
        this.loginExceptionResolver = loginExceptionResolver;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass);
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    protected ApplicationEventPublisher getApplicationEventPublisher() {
        return this.applicationEventPublisher;
    }

    private class InternalCallbackHandler
    implements CallbackHandler {
        private final Authentication authentication;

        InternalCallbackHandler(Authentication authentication) {
            this.authentication = authentication;
        }

        @Override
        public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            for (JaasAuthenticationCallbackHandler handler : AbstractJaasAuthenticationProvider.this.callbackHandlers) {
                for (Callback callback : callbacks) {
                    handler.handle(callback, this.authentication);
                }
            }
        }
    }
}

