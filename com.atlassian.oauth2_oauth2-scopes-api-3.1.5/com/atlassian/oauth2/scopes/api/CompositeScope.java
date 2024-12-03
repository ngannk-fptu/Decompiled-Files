/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.scopes.api;

import com.atlassian.oauth2.scopes.api.Scope;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class CompositeScope
implements Scope {
    private final Set<Scope> scopes;

    public CompositeScope(@Nonnull Set<Scope> scopes) {
        Preconditions.checkArgument((!scopes.isEmpty() ? 1 : 0) != 0, (Object)"Composite scopes cannot be empty");
        this.scopes = scopes;
    }

    @Override
    @Nonnull
    public String getName() {
        return this.scopes.stream().sorted(Comparator.comparing(Scope::getName)).map(Scope::getName).collect(Collectors.joining(" "));
    }

    @Override
    @Nonnull
    public Set<Scope> getScopeAndInheritedScopes() {
        return this.scopes.stream().map(Scope::getScopeAndInheritedScopes).flatMap(Collection::stream).collect(Collectors.toSet());
    }

    public String toString() {
        return this.getName();
    }
}

