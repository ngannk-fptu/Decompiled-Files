/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.Scope
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.scopes.request.bitbucket;

import com.atlassian.oauth2.scopes.api.Scope;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nonnull;

public enum BitbucketScope implements Scope
{
    PUBLIC_REPOS("PUBLIC_REPOS", Collections.emptySet()),
    ACCOUNT_WRITE("ACCOUNT_WRITE", (Set<Scope>)ImmutableSet.of((Object)((Object)PUBLIC_REPOS))),
    REPO_READ("REPO_READ", (Set<Scope>)ImmutableSet.of((Object)((Object)PUBLIC_REPOS))),
    REPO_WRITE("REPO_WRITE", (Set<Scope>)ImmutableSet.of((Object)((Object)REPO_READ), (Object)((Object)PUBLIC_REPOS))),
    REPO_ADMIN("REPO_ADMIN", (Set<Scope>)ImmutableSet.of((Object)((Object)REPO_WRITE), (Object)((Object)REPO_READ), (Object)((Object)PUBLIC_REPOS))),
    PROJECT_ADMIN("PROJECT_ADMIN", (Set<Scope>)ImmutableSet.of((Object)((Object)REPO_ADMIN), (Object)((Object)REPO_WRITE), (Object)((Object)REPO_READ), (Object)((Object)PUBLIC_REPOS))),
    ADMIN_WRITE("ADMIN_WRITE", (Set<Scope>)ImmutableSet.of((Object)((Object)PROJECT_ADMIN), (Object)((Object)REPO_ADMIN), (Object)((Object)REPO_WRITE), (Object)((Object)REPO_READ), (Object)((Object)ACCOUNT_WRITE), (Object)((Object)PUBLIC_REPOS), (Object[])new Scope[0])),
    SYSTEM_ADMIN("SYSTEM_ADMIN", (Set<Scope>)ImmutableSet.of((Object)((Object)ADMIN_WRITE), (Object)((Object)PROJECT_ADMIN), (Object)((Object)REPO_ADMIN), (Object)((Object)REPO_WRITE), (Object)((Object)REPO_READ), (Object)((Object)ACCOUNT_WRITE), (Object[])new Scope[]{PUBLIC_REPOS}));

    private final String name;
    private final Set<Scope> inheritedScopes;

    private BitbucketScope(String name, Set<Scope> inheritedScopes) {
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

