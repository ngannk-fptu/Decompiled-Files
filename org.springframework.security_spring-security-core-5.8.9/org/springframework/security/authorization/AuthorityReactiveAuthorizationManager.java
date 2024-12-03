/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  reactor.core.publisher.Mono
 */
package org.springframework.security.authorization;

import java.util.Collection;
import java.util.List;
import org.springframework.security.authorization.AuthorityAuthorizationDecision;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

public class AuthorityReactiveAuthorizationManager<T>
implements ReactiveAuthorizationManager<T> {
    private final List<GrantedAuthority> authorities;

    AuthorityReactiveAuthorizationManager(String ... authorities) {
        this.authorities = AuthorityUtils.createAuthorityList(authorities);
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, T object) {
        return authentication.filter(Authentication::isAuthenticated).flatMapIterable(Authentication::getAuthorities).map(GrantedAuthority::getAuthority).any(grantedAuthority -> this.authorities.stream().anyMatch(authority -> authority.getAuthority().equals(grantedAuthority))).map(granted -> new AuthorityAuthorizationDecision((boolean)granted, (Collection<GrantedAuthority>)this.authorities)).defaultIfEmpty((Object)new AuthorityAuthorizationDecision(false, this.authorities));
    }

    public static <T> AuthorityReactiveAuthorizationManager<T> hasAuthority(String authority) {
        Assert.notNull((Object)authority, (String)"authority cannot be null");
        return new AuthorityReactiveAuthorizationManager<T>(authority);
    }

    public static <T> AuthorityReactiveAuthorizationManager<T> hasAnyAuthority(String ... authorities) {
        Assert.notNull((Object)authorities, (String)"authorities cannot be null");
        for (String authority : authorities) {
            Assert.notNull((Object)authority, (String)"authority cannot be null");
        }
        return new AuthorityReactiveAuthorizationManager<T>(authorities);
    }

    public static <T> AuthorityReactiveAuthorizationManager<T> hasRole(String role) {
        Assert.notNull((Object)role, (String)"role cannot be null");
        return AuthorityReactiveAuthorizationManager.hasAuthority("ROLE_" + role);
    }

    public static <T> AuthorityReactiveAuthorizationManager<T> hasAnyRole(String ... roles) {
        Assert.notNull((Object)roles, (String)"roles cannot be null");
        for (String role : roles) {
            Assert.notNull((Object)role, (String)"role cannot be null");
        }
        return AuthorityReactiveAuthorizationManager.hasAnyAuthority(AuthorityReactiveAuthorizationManager.toNamedRolesArray(roles));
    }

    private static String[] toNamedRolesArray(String ... roles) {
        String[] result = new String[roles.length];
        for (int i = 0; i < roles.length; ++i) {
            result[i] = "ROLE_" + roles[i];
        }
        return result;
    }
}

