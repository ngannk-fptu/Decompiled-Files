/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.scopes.api;

import com.atlassian.oauth2.scopes.api.ScopeDescription;
import com.atlassian.oauth2.scopes.api.ScopeDescriptionWithTitle;
import java.util.Map;

public interface ScopeDescriptionService {
    public Map<String, ScopeDescriptionWithTitle> getScopeDescriptionsWithTitle();

    public Map<String, ScopeDescription> getScopeDescriptions();
}

