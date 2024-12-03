/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.security.authorization;

import java.util.function.Supplier;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

public final class AuthenticatedAuthorizationManager<T>
implements AuthorizationManager<T> {
    private final AbstractAuthorizationStrategy authorizationStrategy;

    public AuthenticatedAuthorizationManager() {
        this(new AuthenticatedAuthorizationStrategy());
    }

    private AuthenticatedAuthorizationManager(AbstractAuthorizationStrategy authorizationStrategy) {
        this.authorizationStrategy = authorizationStrategy;
    }

    public void setTrustResolver(AuthenticationTrustResolver trustResolver) {
        this.authorizationStrategy.setTrustResolver(trustResolver);
    }

    public static <T> AuthenticatedAuthorizationManager<T> authenticated() {
        return new AuthenticatedAuthorizationManager<T>();
    }

    public static <T> AuthenticatedAuthorizationManager<T> fullyAuthenticated() {
        return new AuthenticatedAuthorizationManager<T>(new FullyAuthenticatedAuthorizationStrategy());
    }

    public static <T> AuthenticatedAuthorizationManager<T> rememberMe() {
        return new AuthenticatedAuthorizationManager<T>(new RememberMeAuthorizationStrategy());
    }

    public static <T> AuthenticatedAuthorizationManager<T> anonymous() {
        return new AuthenticatedAuthorizationManager<T>(new AnonymousAuthorizationStrategy());
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, T object) {
        boolean granted = this.authorizationStrategy.isGranted(authentication.get());
        return new AuthorizationDecision(granted);
    }

    private static final class RememberMeAuthorizationStrategy
    extends AbstractAuthorizationStrategy {
        private RememberMeAuthorizationStrategy() {
        }

        @Override
        boolean isGranted(Authentication authentication) {
            return this.trustResolver.isRememberMe(authentication);
        }
    }

    private static final class AnonymousAuthorizationStrategy
    extends AbstractAuthorizationStrategy {
        private AnonymousAuthorizationStrategy() {
        }

        @Override
        boolean isGranted(Authentication authentication) {
            return this.trustResolver.isAnonymous(authentication);
        }
    }

    private static final class FullyAuthenticatedAuthorizationStrategy
    extends AuthenticatedAuthorizationStrategy {
        private FullyAuthenticatedAuthorizationStrategy() {
        }

        @Override
        boolean isGranted(Authentication authentication) {
            return super.isGranted(authentication) && !this.trustResolver.isRememberMe(authentication);
        }
    }

    private static class AuthenticatedAuthorizationStrategy
    extends AbstractAuthorizationStrategy {
        private AuthenticatedAuthorizationStrategy() {
        }

        @Override
        boolean isGranted(Authentication authentication) {
            return authentication != null && !this.trustResolver.isAnonymous(authentication) && authentication.isAuthenticated();
        }
    }

    private static abstract class AbstractAuthorizationStrategy {
        AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

        private AbstractAuthorizationStrategy() {
        }

        private void setTrustResolver(AuthenticationTrustResolver trustResolver) {
            Assert.notNull((Object)trustResolver, (String)"trustResolver cannot be null");
            this.trustResolver = trustResolver;
        }

        abstract boolean isGranted(Authentication var1);
    }
}

