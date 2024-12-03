/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.scopes.api;

import java.util.Set;
import javax.annotation.Nonnull;

public interface Scope {
    @Nonnull
    public String getName();

    @Nonnull
    public Set<Scope> getScopeAndInheritedScopes();
}

