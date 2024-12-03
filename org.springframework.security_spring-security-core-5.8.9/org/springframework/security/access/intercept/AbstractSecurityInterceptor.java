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
 *  org.springframework.context.MessageSource
 *  org.springframework.context.MessageSourceAware
 *  org.springframework.context.support.MessageSourceAccessor
 *  org.springframework.core.log.LogMessage
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 */
package org.springframework.security.access.intercept;

import java.util.Collection;
import java.util.HashSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.log.LogMessage;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.event.AuthenticationCredentialsNotFoundEvent;
import org.springframework.security.access.event.AuthorizationFailureEvent;
import org.springframework.security.access.event.AuthorizedEvent;
import org.springframework.security.access.event.PublicInvocationEvent;
import org.springframework.security.access.intercept.AfterInvocationManager;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.access.intercept.NullRunAsManager;
import org.springframework.security.access.intercept.RunAsManager;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

@Deprecated
public abstract class AbstractSecurityInterceptor
implements InitializingBean,
ApplicationEventPublisherAware,
MessageSourceAware {
    protected final Log logger = LogFactory.getLog(this.getClass());
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private ApplicationEventPublisher eventPublisher;
    private AccessDecisionManager accessDecisionManager;
    private AfterInvocationManager afterInvocationManager;
    private AuthenticationManager authenticationManager = new NoOpAuthenticationManager();
    private RunAsManager runAsManager = new NullRunAsManager();
    private boolean alwaysReauthenticate = false;
    private boolean rejectPublicInvocations = false;
    private boolean validateConfigAttributes = true;
    private boolean publishAuthorizationSuccess = false;

    public void afterPropertiesSet() {
        Assert.notNull(this.getSecureObjectClass(), (String)"Subclass must provide a non-null response to getSecureObjectClass()");
        Assert.notNull((Object)this.messages, (String)"A message source must be set");
        Assert.notNull((Object)this.authenticationManager, (String)"An AuthenticationManager is required");
        Assert.notNull((Object)this.accessDecisionManager, (String)"An AccessDecisionManager is required");
        Assert.notNull((Object)this.runAsManager, (String)"A RunAsManager is required");
        Assert.notNull((Object)this.obtainSecurityMetadataSource(), (String)"An SecurityMetadataSource is required");
        Assert.isTrue((boolean)this.obtainSecurityMetadataSource().supports(this.getSecureObjectClass()), () -> "SecurityMetadataSource does not support secure object class: " + this.getSecureObjectClass());
        Assert.isTrue((boolean)this.runAsManager.supports(this.getSecureObjectClass()), () -> "RunAsManager does not support secure object class: " + this.getSecureObjectClass());
        Assert.isTrue((boolean)this.accessDecisionManager.supports(this.getSecureObjectClass()), () -> "AccessDecisionManager does not support secure object class: " + this.getSecureObjectClass());
        if (this.afterInvocationManager != null) {
            Assert.isTrue((boolean)this.afterInvocationManager.supports(this.getSecureObjectClass()), () -> "AfterInvocationManager does not support secure object class: " + this.getSecureObjectClass());
        }
        if (this.validateConfigAttributes) {
            Collection<ConfigAttribute> attributeDefs = this.obtainSecurityMetadataSource().getAllConfigAttributes();
            if (attributeDefs == null) {
                this.logger.warn((Object)"Could not validate configuration attributes as the SecurityMetadataSource did not return any attributes from getAllConfigAttributes()");
                return;
            }
            this.validateAttributeDefs(attributeDefs);
        }
    }

    private void validateAttributeDefs(Collection<ConfigAttribute> attributeDefs) {
        HashSet<ConfigAttribute> unsupportedAttrs = new HashSet<ConfigAttribute>();
        for (ConfigAttribute attr : attributeDefs) {
            if (this.runAsManager.supports(attr) || this.accessDecisionManager.supports(attr) || this.afterInvocationManager != null && this.afterInvocationManager.supports(attr)) continue;
            unsupportedAttrs.add(attr);
        }
        if (unsupportedAttrs.size() != 0) {
            this.logger.trace((Object)"Did not validate configuration attributes since validateConfigurationAttributes is false");
            throw new IllegalArgumentException("Unsupported configuration attributes: " + unsupportedAttrs);
        }
        this.logger.trace((Object)"Validated configuration attributes");
    }

    protected InterceptorStatusToken beforeInvocation(Object object) {
        Authentication runAs;
        Assert.notNull((Object)object, (String)"Object was null");
        if (!this.getSecureObjectClass().isAssignableFrom(object.getClass())) {
            throw new IllegalArgumentException("Security invocation attempted for object " + object.getClass().getName() + " but AbstractSecurityInterceptor only configured to support secure objects of type: " + this.getSecureObjectClass());
        }
        Collection<ConfigAttribute> attributes = this.obtainSecurityMetadataSource().getAttributes(object);
        if (CollectionUtils.isEmpty(attributes)) {
            Assert.isTrue((!this.rejectPublicInvocations ? 1 : 0) != 0, () -> "Secure object invocation " + object + " was denied as public invocations are not allowed via this interceptor. This indicates a configuration error because the rejectPublicInvocations property is set to 'true'");
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)LogMessage.format((String)"Authorized public object %s", (Object)object));
            }
            this.publishEvent(new PublicInvocationEvent(object));
            return null;
        }
        if (this.securityContextHolderStrategy.getContext().getAuthentication() == null) {
            this.credentialsNotFound(this.messages.getMessage("AbstractSecurityInterceptor.authenticationNotFound", "An Authentication object was not found in the SecurityContext"), object, attributes);
        }
        Authentication authenticated = this.authenticateIfRequired();
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)LogMessage.format((String)"Authorizing %s with attributes %s", (Object)object, attributes));
        }
        this.attemptAuthorization(object, attributes, authenticated);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)LogMessage.format((String)"Authorized %s with attributes %s", (Object)object, attributes));
        }
        if (this.publishAuthorizationSuccess) {
            this.publishEvent(new AuthorizedEvent(object, attributes, authenticated));
        }
        if ((runAs = this.runAsManager.buildRunAs(authenticated, object, attributes)) != null) {
            SecurityContext origCtx = this.securityContextHolderStrategy.getContext();
            SecurityContext newCtx = this.securityContextHolderStrategy.createEmptyContext();
            newCtx.setAuthentication(runAs);
            this.securityContextHolderStrategy.setContext(newCtx);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)LogMessage.format((String)"Switched to RunAs authentication %s", (Object)runAs));
            }
            return new InterceptorStatusToken(origCtx, true, attributes, object);
        }
        this.logger.trace((Object)"Did not switch RunAs authentication since RunAsManager returned null");
        return new InterceptorStatusToken(this.securityContextHolderStrategy.getContext(), false, attributes, object);
    }

    private void attemptAuthorization(Object object, Collection<ConfigAttribute> attributes, Authentication authenticated) {
        try {
            this.accessDecisionManager.decide(authenticated, object, attributes);
        }
        catch (AccessDeniedException ex) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)LogMessage.format((String)"Failed to authorize %s with attributes %s using %s", (Object)object, attributes, (Object)this.accessDecisionManager));
            } else if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)LogMessage.format((String)"Failed to authorize %s with attributes %s", (Object)object, attributes));
            }
            this.publishEvent(new AuthorizationFailureEvent(object, attributes, authenticated, ex));
            throw ex;
        }
    }

    protected void finallyInvocation(InterceptorStatusToken token) {
        if (token != null && token.isContextHolderRefreshRequired()) {
            this.securityContextHolderStrategy.setContext(token.getSecurityContext());
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)LogMessage.of(() -> "Reverted to original authentication " + token.getSecurityContext().getAuthentication()));
            }
        }
    }

    protected Object afterInvocation(InterceptorStatusToken token, Object returnedObject) {
        if (token == null) {
            return returnedObject;
        }
        this.finallyInvocation(token);
        if (this.afterInvocationManager != null) {
            try {
                returnedObject = this.afterInvocationManager.decide(token.getSecurityContext().getAuthentication(), token.getSecureObject(), token.getAttributes(), returnedObject);
            }
            catch (AccessDeniedException ex) {
                this.publishEvent(new AuthorizationFailureEvent(token.getSecureObject(), token.getAttributes(), token.getSecurityContext().getAuthentication(), ex));
                throw ex;
            }
        }
        return returnedObject;
    }

    private Authentication authenticateIfRequired() {
        Authentication authentication = this.securityContextHolderStrategy.getContext().getAuthentication();
        if (authentication.isAuthenticated() && !this.alwaysReauthenticate) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)LogMessage.format((String)"Did not re-authenticate %s before authorizing", (Object)authentication));
            }
            return authentication;
        }
        authentication = this.authenticationManager.authenticate(authentication);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)LogMessage.format((String)"Re-authenticated %s before authorizing", (Object)authentication));
        }
        SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        this.securityContextHolderStrategy.setContext(context);
        return authentication;
    }

    private void credentialsNotFound(String reason, Object secureObject, Collection<ConfigAttribute> configAttribs) {
        AuthenticationCredentialsNotFoundException exception = new AuthenticationCredentialsNotFoundException(reason);
        AuthenticationCredentialsNotFoundEvent event = new AuthenticationCredentialsNotFoundEvent(secureObject, configAttribs, exception);
        this.publishEvent(event);
        throw exception;
    }

    public AccessDecisionManager getAccessDecisionManager() {
        return this.accessDecisionManager;
    }

    public AfterInvocationManager getAfterInvocationManager() {
        return this.afterInvocationManager;
    }

    public AuthenticationManager getAuthenticationManager() {
        return this.authenticationManager;
    }

    public RunAsManager getRunAsManager() {
        return this.runAsManager;
    }

    public abstract Class<?> getSecureObjectClass();

    public boolean isAlwaysReauthenticate() {
        return this.alwaysReauthenticate;
    }

    public boolean isRejectPublicInvocations() {
        return this.rejectPublicInvocations;
    }

    public boolean isValidateConfigAttributes() {
        return this.validateConfigAttributes;
    }

    public abstract SecurityMetadataSource obtainSecurityMetadataSource();

    public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
        Assert.notNull((Object)securityContextHolderStrategy, (String)"securityContextHolderStrategy cannot be null");
        this.securityContextHolderStrategy = securityContextHolderStrategy;
    }

    public void setAccessDecisionManager(AccessDecisionManager accessDecisionManager) {
        this.accessDecisionManager = accessDecisionManager;
    }

    public void setAfterInvocationManager(AfterInvocationManager afterInvocationManager) {
        this.afterInvocationManager = afterInvocationManager;
    }

    public void setAlwaysReauthenticate(boolean alwaysReauthenticate) {
        this.alwaysReauthenticate = alwaysReauthenticate;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }

    public void setAuthenticationManager(AuthenticationManager newManager) {
        this.authenticationManager = newManager;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public void setPublishAuthorizationSuccess(boolean publishAuthorizationSuccess) {
        this.publishAuthorizationSuccess = publishAuthorizationSuccess;
    }

    public void setRejectPublicInvocations(boolean rejectPublicInvocations) {
        this.rejectPublicInvocations = rejectPublicInvocations;
    }

    public void setRunAsManager(RunAsManager runAsManager) {
        this.runAsManager = runAsManager;
    }

    public void setValidateConfigAttributes(boolean validateConfigAttributes) {
        this.validateConfigAttributes = validateConfigAttributes;
    }

    private void publishEvent(ApplicationEvent event) {
        if (this.eventPublisher != null) {
            this.eventPublisher.publishEvent(event);
        }
    }

    private static class NoOpAuthenticationManager
    implements AuthenticationManager {
        private NoOpAuthenticationManager() {
        }

        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            throw new AuthenticationServiceException("Cannot authenticate " + authentication);
        }
    }
}

