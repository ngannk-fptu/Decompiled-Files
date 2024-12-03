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
package org.springframework.security.authentication;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.util.Assert;

public class RememberMeAuthenticationProvider
implements AuthenticationProvider,
InitializingBean,
MessageSourceAware {
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private String key;

    public RememberMeAuthenticationProvider(String key) {
        Assert.hasLength((String)key, (String)"key must have a length");
        this.key = key;
    }

    public void afterPropertiesSet() {
        Assert.notNull((Object)this.messages, (String)"A message source must be set");
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!this.supports(authentication.getClass())) {
            return null;
        }
        if (this.key.hashCode() != ((RememberMeAuthenticationToken)authentication).getKeyHash()) {
            throw new BadCredentialsException(this.messages.getMessage("RememberMeAuthenticationProvider.incorrectKey", "The presented RememberMeAuthenticationToken does not contain the expected key"));
        }
        return authentication;
    }

    public String getKey() {
        return this.key;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return RememberMeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

