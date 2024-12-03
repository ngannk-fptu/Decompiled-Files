/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.security.authorization;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import org.springframework.security.access.hierarchicalroles.NullRoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authorization.AuthorityAuthorizationDecision;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.util.Assert;

public final class AuthorityAuthorizationManager<T>
implements AuthorizationManager<T> {
    private static final String ROLE_PREFIX = "ROLE_";
    private final List<GrantedAuthority> authorities;
    private RoleHierarchy roleHierarchy = new NullRoleHierarchy();

    private AuthorityAuthorizationManager(String ... authorities) {
        this.authorities = AuthorityUtils.createAuthorityList(authorities);
    }

    public void setRoleHierarchy(RoleHierarchy roleHierarchy) {
        Assert.notNull((Object)roleHierarchy, (String)"roleHierarchy cannot be null");
        this.roleHierarchy = roleHierarchy;
    }

    public static <T> AuthorityAuthorizationManager<T> hasRole(String role) {
        Assert.notNull((Object)role, (String)"role cannot be null");
        Assert.isTrue((!role.startsWith(ROLE_PREFIX) ? 1 : 0) != 0, () -> role + " should not start with " + ROLE_PREFIX + " since " + ROLE_PREFIX + " is automatically prepended when using hasRole. Consider using hasAuthority instead.");
        return AuthorityAuthorizationManager.hasAuthority(ROLE_PREFIX + role);
    }

    public static <T> AuthorityAuthorizationManager<T> hasAuthority(String authority) {
        Assert.notNull((Object)authority, (String)"authority cannot be null");
        return new AuthorityAuthorizationManager<T>(authority);
    }

    public static <T> AuthorityAuthorizationManager<T> hasAnyRole(String ... roles) {
        return AuthorityAuthorizationManager.hasAnyRole(ROLE_PREFIX, roles);
    }

    public static <T> AuthorityAuthorizationManager<T> hasAnyRole(String rolePrefix, String[] roles) {
        Assert.notNull((Object)rolePrefix, (String)"rolePrefix cannot be null");
        Assert.notEmpty((Object[])roles, (String)"roles cannot be empty");
        Assert.noNullElements((Object[])roles, (String)"roles cannot contain null values");
        return AuthorityAuthorizationManager.hasAnyAuthority(AuthorityAuthorizationManager.toNamedRolesArray(rolePrefix, roles));
    }

    public static <T> AuthorityAuthorizationManager<T> hasAnyAuthority(String ... authorities) {
        Assert.notEmpty((Object[])authorities, (String)"authorities cannot be empty");
        Assert.noNullElements((Object[])authorities, (String)"authorities cannot contain null values");
        return new AuthorityAuthorizationManager<T>(authorities);
    }

    private static String[] toNamedRolesArray(String rolePrefix, String[] roles) {
        String[] result = new String[roles.length];
        for (int i = 0; i < roles.length; ++i) {
            String role = roles[i];
            Assert.isTrue((!role.startsWith(rolePrefix) ? 1 : 0) != 0, () -> role + " should not start with " + rolePrefix + " since " + rolePrefix + " is automatically prepended when using hasAnyRole. Consider using hasAnyAuthority instead.");
            result[i] = rolePrefix + role;
        }
        return result;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, T object) {
        boolean granted = this.isGranted(authentication.get());
        return new AuthorityAuthorizationDecision(granted, this.authorities);
    }

    private boolean isGranted(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated() && this.isAuthorized(authentication);
    }

    private boolean isAuthorized(Authentication authentication) {
        Set<String> authorities = AuthorityUtils.authorityListToSet(this.authorities);
        for (GrantedAuthority grantedAuthority : this.getGrantedAuthorities(authentication)) {
            if (!authorities.contains(grantedAuthority.getAuthority())) continue;
            return true;
        }
        return false;
    }

    private Collection<? extends GrantedAuthority> getGrantedAuthorities(Authentication authentication) {
        return this.roleHierarchy.getReachableGrantedAuthorities(authentication.getAuthorities());
    }

    public String toString() {
        return "AuthorityAuthorizationManager[authorities=" + this.authorities + "]";
    }
}

