/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.MessageSource
 *  org.springframework.context.MessageSourceAware
 *  org.springframework.context.support.MessageSourceAccessor
 *  org.springframework.util.Assert
 */
package org.springframework.security.authentication;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.util.Assert;

public class AnonymousAuthenticationProvider
implements AuthenticationProvider,
MessageSourceAware {
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private String key;

    public AnonymousAuthenticationProvider(String key) {
        Assert.hasLength((String)key, (String)"A Key is required");
        this.key = key;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!this.supports(authentication.getClass())) {
            return null;
        }
        if (this.key.hashCode() != ((AnonymousAuthenticationToken)authentication).getKeyHash()) {
            throw new BadCredentialsException(this.messages.getMessage("AnonymousAuthenticationProvider.incorrectKey", "The presented AnonymousAuthenticationToken does not contain the expected key"));
        }
        return authentication;
    }

    public String getKey() {
        return this.key;
    }

    public void setMessageSource(MessageSource messageSource) {
        Assert.notNull((Object)messageSource, (String)"messageSource cannot be null");
        this.messages = new MessageSourceAccessor(messageSource);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AnonymousAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

