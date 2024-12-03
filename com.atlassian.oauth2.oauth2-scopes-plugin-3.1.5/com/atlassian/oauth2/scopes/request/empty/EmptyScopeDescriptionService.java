/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.ScopeDescription
 *  com.atlassian.oauth2.scopes.api.ScopeDescriptionWithTitle
 *  com.atlassian.oauth2.scopes.api.ScopeResolver
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.oauth2.scopes.request.empty;

import com.atlassian.oauth2.scopes.api.ScopeDescription;
import com.atlassian.oauth2.scopes.api.ScopeDescriptionWithTitle;
import com.atlassian.oauth2.scopes.api.ScopeResolver;
import com.atlassian.oauth2.scopes.request.AbstractScopeDescriptionService;
import com.atlassian.oauth2.scopes.request.empty.EmptyScopeResolver;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class EmptyScopeDescriptionService
extends AbstractScopeDescriptionService {
    final I18nResolver i18nResolver;

    public EmptyScopeDescriptionService(I18nResolver i18nResolver, ApplicationProperties applicationProperties, ScopeResolver scopeResolver) {
        super(i18nResolver, applicationProperties, scopeResolver);
        this.i18nResolver = i18nResolver;
    }

    public Map<String, ScopeDescriptionWithTitle> getScopeDescriptionsWithTitle() {
        return ImmutableMap.of((Object)new EmptyScopeResolver.EmptyScope().getName(), (Object)new ScopeDescriptionWithTitle(new EmptyScopeResolver.EmptyScope().getName(), this.i18nResolver.getText("scopes.oauth2.bamboo.permissions.all.description")));
    }

    public Map<String, ScopeDescription> getScopeDescriptions() {
        return ImmutableMap.of((Object)new EmptyScopeResolver.EmptyScope().getName(), (Object)new ScopeDescription(this.i18nResolver.getText("scopes.oauth2.bamboo.permissions.all.description")));
    }

    @Override
    protected String getI18nKey() {
        return "";
    }
}

