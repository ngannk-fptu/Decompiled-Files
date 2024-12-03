/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.Scope
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.scopes.request.basic;

import com.atlassian.oauth2.scopes.api.Scope;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;

public enum BasicScope implements Scope
{
    READ("READ", Collections.emptySet()),
    WRITE("WRITE", (Set<Scope>)ImmutableSet.of((Object)((Object)READ))),
    ADMIN("ADMIN", (Set<Scope>)ImmutableSet.of((Object)((Object)WRITE), (Object)((Object)READ))),
    SYSTEM_ADMIN("SYSTEM_ADMIN", (Set<Scope>)ImmutableSet.of((Object)((Object)ADMIN), (Object)((Object)WRITE), (Object)((Object)READ)));

    private final String name;
    private final Set<Scope> inheritedScopes;

    private BasicScope(String name, Set<Scope> inheritedScopes) {
        this.name = name;
        this.inheritedScopes = ImmutableSet.builder().add((Object)this).addAll(inheritedScopes).build();
    }

    public static Optional<BasicScope> get(Scope scope) {
        return Arrays.stream(BasicScope.values()).filter(basicScope -> basicScope.equals(scope)).findFirst();
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    @Nonnull
    public Set<Scope> getScopeAndInheritedScopes() {
        return this.inheritedScopes;
    }

    public String toString() {
        return this.name;
    }
}

