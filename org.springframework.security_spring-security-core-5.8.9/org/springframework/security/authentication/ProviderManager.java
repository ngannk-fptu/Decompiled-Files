/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.context.MessageSource
 *  org.springframework.context.MessageSourceAware
 *  org.springframework.context.support.MessageSourceAccessor
 *  org.springframework.core.log.LogMessage
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 */
package org.springframework.security.authentication;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.log.LogMessage;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

public class ProviderManager
implements AuthenticationManager,
MessageSourceAware,
InitializingBean {
    private static final Log logger = LogFactory.getLog(ProviderManager.class);
    private AuthenticationEventPublisher eventPublisher = new NullEventPublisher();
    private List<AuthenticationProvider> providers = Collections.emptyList();
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private AuthenticationManager parent;
    private boolean eraseCredentialsAfterAuthentication = true;

    public ProviderManager(AuthenticationProvider ... providers) {
        this(Arrays.asList(providers), null);
    }

    public ProviderManager(List<AuthenticationProvider> providers) {
        this(providers, null);
    }

    public ProviderManager(List<AuthenticationProvider> providers, AuthenticationManager parent) {
        Assert.notNull(providers, (String)"providers list cannot be null");
        this.providers = providers;
        this.parent = parent;
        this.checkState();
    }

    public void afterPropertiesSet() {
        this.checkState();
    }

    private void checkState() {
        Assert.isTrue((this.parent != null || !this.providers.isEmpty() ? 1 : 0) != 0, (String)"A parent AuthenticationManager or a list of AuthenticationProviders is required");
        Assert.isTrue((!CollectionUtils.contains(this.providers.iterator(), null) ? 1 : 0) != 0, (String)"providers list cannot contain null values");
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Class<?> toTest = authentication.getClass();
        AuthenticationException lastException = null;
        AuthenticationException parentException = null;
        Authentication result = null;
        Authentication parentResult = null;
        int currentPosition = 0;
        int size = this.providers.size();
        for (AuthenticationProvider provider : this.getProviders()) {
            if (!provider.supports(toTest)) continue;
            if (logger.isTraceEnabled()) {
                logger.trace((Object)LogMessage.format((String)"Authenticating request with %s (%d/%d)", (Object)provider.getClass().getSimpleName(), (Object)(++currentPosition), (Object)size));
            }
            try {
                result = provider.authenticate(authentication);
                if (result == null) continue;
                this.copyDetails(authentication, result);
                break;
            }
            catch (AccountStatusException | InternalAuthenticationServiceException ex) {
                this.prepareException(ex, authentication);
                throw ex;
            }
            catch (AuthenticationException ex) {
                lastException = ex;
            }
        }
        if (result == null && this.parent != null) {
            try {
                result = parentResult = this.parent.authenticate(authentication);
            }
            catch (ProviderNotFoundException providerNotFoundException) {
            }
            catch (AuthenticationException ex) {
                parentException = ex;
                lastException = ex;
            }
        }
        if (result != null) {
            if (this.eraseCredentialsAfterAuthentication && result instanceof CredentialsContainer) {
                ((CredentialsContainer)((Object)result)).eraseCredentials();
            }
            if (parentResult == null) {
                this.eventPublisher.publishAuthenticationSuccess(result);
            }
            return result;
        }
        if (lastException == null) {
            lastException = new ProviderNotFoundException(this.messages.getMessage("ProviderManager.providerNotFound", new Object[]{toTest.getName()}, "No AuthenticationProvider found for {0}"));
        }
        if (parentException == null) {
            this.prepareException(lastException, authentication);
        }
        throw lastException;
    }

    private void prepareException(AuthenticationException ex, Authentication auth) {
        this.eventPublisher.publishAuthenticationFailure(ex, auth);
    }

    private void copyDetails(Authentication source, Authentication dest) {
        if (dest instanceof AbstractAuthenticationToken && dest.getDetails() == null) {
            AbstractAuthenticationToken token = (AbstractAuthenticationToken)dest;
            token.setDetails(source.getDetails());
        }
    }

    public List<AuthenticationProvider> getProviders() {
        return this.providers;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public void setAuthenticationEventPublisher(AuthenticationEventPublisher eventPublisher) {
        Assert.notNull((Object)eventPublisher, (String)"AuthenticationEventPublisher cannot be null");
        this.eventPublisher = eventPublisher;
    }

    public void setEraseCredentialsAfterAuthentication(boolean eraseSecretData) {
        this.eraseCredentialsAfterAuthentication = eraseSecretData;
    }

    public boolean isEraseCredentialsAfterAuthentication() {
        return this.eraseCredentialsAfterAuthentication;
    }

    private static final class NullEventPublisher
    implements AuthenticationEventPublisher {
        private NullEventPublisher() {
        }

        @Override
        public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
        }

        @Override
        public void publishAuthenticationSuccess(Authentication authentication) {
        }
    }
}

