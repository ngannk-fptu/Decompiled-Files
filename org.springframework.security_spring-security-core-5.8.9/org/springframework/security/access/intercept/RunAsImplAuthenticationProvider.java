/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.context.MessageSource
 *  org.springframework.context.MessageSourceAware
 *  org.springframework.context.support.MessageSourceAccessor
 *  org.springframework.util.Assert
 */
package org.springframework.security.access.intercept;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.util.Assert;

@Deprecated
public class RunAsImplAuthenticationProvider
implements InitializingBean,
AuthenticationProvider,
MessageSourceAware {
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private String key;

    public void afterPropertiesSet() {
        Assert.notNull((Object)this.key, (String)"A Key is required and should match that configured for the RunAsManagerImpl");
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        RunAsUserToken token = (RunAsUserToken)authentication;
        if (token.getKeyHash() != this.key.hashCode()) {
            throw new BadCredentialsException(this.messages.getMessage("RunAsImplAuthenticationProvider.incorrectKey", "The presented RunAsUserToken does not contain the expected key"));
        }
        return authentication;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return RunAsUserToken.class.isAssignableFrom(authentication);
    }
}

