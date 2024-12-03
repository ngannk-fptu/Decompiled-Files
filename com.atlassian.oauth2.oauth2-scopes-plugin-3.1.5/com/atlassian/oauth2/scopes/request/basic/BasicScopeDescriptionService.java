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
package com.atlassian.oauth2.scopes.request.basic;

import com.atlassian.oauth2.scopes.api.ScopeDescription;
import com.atlassian.oauth2.scopes.api.ScopeDescriptionWithTitle;
import com.atlassian.oauth2.scopes.api.ScopeResolver;
import com.atlassian.oauth2.scopes.request.AbstractScopeDescriptionService;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.stream.Collectors;

public class BasicScopeDescriptionService
extends AbstractScopeDescriptionService {
    private static final String BASE_I18N_KEY = "scopes.oauth2.scope.level.";

    public BasicScopeDescriptionService(I18nResolver i18n, ApplicationProperties applicationProperties, ScopeResolver scopeResolver) {
        super(i18n, applicationProperties, scopeResolver);
    }

    @Override
    protected String getI18nKey() {
        return BASE_I18N_KEY;
    }

    public Map<String, ScopeDescriptionWithTitle> getScopeDescriptionsWithTitle() {
        return this.getScopeDescriptions(this::getDescriptionsAndTitleFilteredByScope);
    }

    public Map<String, ScopeDescription> getScopeDescriptions() {
        Map<String, ScopeDescription> descriptions = this.getScopeDescriptions(this::getDescriptionsAndTitleFilteredByScope).entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new ScopeDescription(((ScopeDescriptionWithTitle)entry.getValue()).getDescription())));
        return ImmutableMap.copyOf(descriptions);
    }
}

