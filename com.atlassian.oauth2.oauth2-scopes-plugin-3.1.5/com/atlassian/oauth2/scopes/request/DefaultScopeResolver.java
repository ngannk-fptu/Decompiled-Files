/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.CompositeScope
 *  com.atlassian.oauth2.scopes.api.InvalidScopeException
 *  com.atlassian.oauth2.scopes.api.Scope
 *  com.atlassian.oauth2.scopes.api.ScopeResolver
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.oauth2.scopes.request;

import com.atlassian.oauth2.scopes.api.CompositeScope;
import com.atlassian.oauth2.scopes.api.InvalidScopeException;
import com.atlassian.oauth2.scopes.api.Scope;
import com.atlassian.oauth2.scopes.api.ScopeResolver;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

public class DefaultScopeResolver
implements ScopeResolver {
    private final Set<Scope> availableScopes;
    private final Set<String> availableScopeNames;

    public DefaultScopeResolver(@Nonnull Set<Scope> availableScopes) {
        this.availableScopes = ImmutableSet.builder().addAll(availableScopes).build();
        this.availableScopeNames = availableScopes.stream().map(Scope::getName).collect(Collectors.toSet());
    }

    public Scope getScope(String scopeName) throws InvalidScopeException {
        if (StringUtils.isBlank((CharSequence)scopeName)) {
            throw InvalidScopeException.blankScope();
        }
        Set scopeNames = Arrays.stream(scopeName.split(" ")).collect(Collectors.toSet());
        Sets.SetView invalidScopes = Sets.difference(scopeNames, this.availableScopeNames);
        if (!invalidScopes.isEmpty()) {
            throw new InvalidScopeException((Collection)invalidScopes);
        }
        Set scopes = this.availableScopes.stream().filter(scope -> scopeNames.contains(scope.getName())).collect(Collectors.toSet());
        if (scopes.size() > 1) {
            return new CompositeScope(scopes);
        }
        return (Scope)scopes.iterator().next();
    }

    public boolean hasScopePermission(Scope tokenScope, Scope configScope) {
        return configScope.getScopeAndInheritedScopes().containsAll(tokenScope.getScopeAndInheritedScopes());
    }

    public Set<Scope> getAvailableScopes() {
        return this.availableScopes;
    }
}

