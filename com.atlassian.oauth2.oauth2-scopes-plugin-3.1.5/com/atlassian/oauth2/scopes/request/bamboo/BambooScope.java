/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.Scope
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.scopes.request.bamboo;

import com.atlassian.oauth2.scopes.api.Scope;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nonnull;

public enum BambooScope implements Scope
{
    READ("READ", Collections.emptySet()),
    TRIGGER("TRIGGER", (Set<Scope>)ImmutableSet.of((Object)((Object)READ))),
    USER("USER", (Set<Scope>)ImmutableSet.of((Object)((Object)TRIGGER), (Object)((Object)READ)));

    private final String name;
    private final Set<Scope> inheritedScopes;

    private BambooScope(String name, Set<Scope> inheritedScopes) {
        this.name = name;
        this.inheritedScopes = ImmutableSet.builder().add((Object)this).addAll(inheritedScopes).build();
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

