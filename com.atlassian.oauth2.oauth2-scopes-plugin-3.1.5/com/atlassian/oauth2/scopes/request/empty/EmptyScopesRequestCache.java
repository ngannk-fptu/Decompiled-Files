/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.Permission
 *  com.atlassian.oauth2.scopes.api.Scope
 */
package com.atlassian.oauth2.scopes.request.empty;

import com.atlassian.oauth2.scopes.api.Permission;
import com.atlassian.oauth2.scopes.api.Scope;
import com.atlassian.oauth2.scopes.request.DefaultScopesRequestCache;
import java.util.Collections;
import java.util.List;

public class EmptyScopesRequestCache
extends DefaultScopesRequestCache {
    @Override
    protected List<Permission> getPermissions(Scope scope) {
        return Collections.emptyList();
    }
}

