/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.security.access.vote;

import java.util.Collection;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

@Deprecated
public class AuthenticatedVoter
implements AccessDecisionVoter<Object> {
    public static final String IS_AUTHENTICATED_FULLY = "IS_AUTHENTICATED_FULLY";
    public static final String IS_AUTHENTICATED_REMEMBERED = "IS_AUTHENTICATED_REMEMBERED";
    public static final String IS_AUTHENTICATED_ANONYMOUSLY = "IS_AUTHENTICATED_ANONYMOUSLY";
    private AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

    private boolean isFullyAuthenticated(Authentication authentication) {
        return !this.authenticationTrustResolver.isAnonymous(authentication) && !this.authenticationTrustResolver.isRememberMe(authentication);
    }

    public void setAuthenticationTrustResolver(AuthenticationTrustResolver authenticationTrustResolver) {
        Assert.notNull((Object)authenticationTrustResolver, (String)"AuthenticationTrustResolver cannot be set to null");
        this.authenticationTrustResolver = authenticationTrustResolver;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return attribute.getAttribute() != null && (IS_AUTHENTICATED_FULLY.equals(attribute.getAttribute()) || IS_AUTHENTICATED_REMEMBERED.equals(attribute.getAttribute()) || IS_AUTHENTICATED_ANONYMOUSLY.equals(attribute.getAttribute()));
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        int result = 0;
        for (ConfigAttribute attribute : attributes) {
            if (!this.supports(attribute)) continue;
            result = -1;
            if (IS_AUTHENTICATED_FULLY.equals(attribute.getAttribute()) && this.isFullyAuthenticated(authentication)) {
                return 1;
            }
            if (IS_AUTHENTICATED_REMEMBERED.equals(attribute.getAttribute()) && (this.authenticationTrustResolver.isRememberMe(authentication) || this.isFullyAuthenticated(authentication))) {
                return 1;
            }
            if (!IS_AUTHENTICATED_ANONYMOUSLY.equals(attribute.getAttribute()) || !this.authenticationTrustResolver.isAnonymous(authentication) && !this.isFullyAuthenticated(authentication) && !this.authenticationTrustResolver.isRememberMe(authentication)) continue;
            return 1;
        }
        return result;
    }
}

