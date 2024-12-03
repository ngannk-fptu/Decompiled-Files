/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.InvalidScopeException
 *  com.atlassian.oauth2.scopes.api.Scope
 *  com.atlassian.oauth2.scopes.api.ScopeResolver
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.scopes.request.empty;

import com.atlassian.oauth2.scopes.api.InvalidScopeException;
import com.atlassian.oauth2.scopes.api.Scope;
import com.atlassian.oauth2.scopes.api.ScopeResolver;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import javax.annotation.Nonnull;

public class EmptyScopeResolver
implements ScopeResolver {
    public Scope getScope(String scopeName) throws InvalidScopeException {
        return new EmptyScope();
    }

    public boolean hasScopePermission(Scope tokenScope, Scope configScope) {
        return true;
    }

    public Set<Scope> getAvailableScopes() {
        return ImmutableSet.of((Object)new EmptyScope());
    }

    public static class EmptyScope
    implements Scope {
        @Nonnull
        public String getName() {
            return "ALL";
        }

        @Nonnull
        public Set<Scope> getScopeAndInheritedScopes() {
            return ImmutableSet.of((Object)this);
        }

        public int hashCode() {
            return super.hashCode();
        }

        public boolean equals(Object obj) {
            return obj != null && obj.getClass().equals(this.getClass());
        }

        public String toString() {
            return this.getName();
        }
    }
}

