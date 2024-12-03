/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.ScopeDescription
 *  com.atlassian.oauth2.scopes.api.ScopeDescriptionWithTitle
 *  com.atlassian.oauth2.scopes.api.ScopeResolver
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  io.atlassian.util.concurrent.LazyReference
 */
package com.atlassian.oauth2.scopes.request.bitbucket;

import com.atlassian.oauth2.scopes.api.ScopeDescription;
import com.atlassian.oauth2.scopes.api.ScopeDescriptionWithTitle;
import com.atlassian.oauth2.scopes.api.ScopeResolver;
import com.atlassian.oauth2.scopes.request.AbstractScopeDescriptionService;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.atlassian.util.concurrent.LazyReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class BitbucketScopeDescriptionService
extends AbstractScopeDescriptionService {
    private static final String BASE_CONSENT_I18N_KEY = "consent.oauth2.scope.level.";
    private final I18nResolver i18nResolver;
    final LazyReference<Map<String, String>> simpleScopeDescriptions = new LazyReference(){

        protected Map<String, String> create() {
            return BitbucketScopeDescriptionService.this.getSimplifiedDescriptionI18nStrings();
        }
    };

    public BitbucketScopeDescriptionService(I18nResolver i18n, ApplicationProperties applicationProperties, ScopeResolver scopeResolver) {
        super(i18n, applicationProperties, scopeResolver);
        this.i18nResolver = i18n;
    }

    public Map<String, ScopeDescription> getScopeDescriptions() {
        return this.getScopeDescriptions(this::getSimplifiedDescriptionsByScope);
    }

    public Map<String, ScopeDescriptionWithTitle> getScopeDescriptionsWithTitle() {
        return this.getScopeDescriptions(this::getDescriptionsAndTitleFilteredByScope);
    }

    private ScopeDescription getSimplifiedDescriptionsByScope(String scope) {
        List descriptions = ((Map)Objects.requireNonNull(this.simpleScopeDescriptions.get())).entrySet().stream().filter(i18nKey -> ((String)i18nKey.getKey()).contains(scope.toLowerCase())).map(Map.Entry::getValue).collect(Collectors.toList());
        return new ScopeDescription(descriptions);
    }

    @Override
    protected String getI18nKey() {
        return BASE_CONSENT_I18N_KEY;
    }

    private Map<String, String> getSimplifiedDescriptionI18nStrings() {
        HashMap i18nStrings = new HashMap();
        ImmutableList i18nScopeKeys = ImmutableList.of((Object)"scopes.oauth2.permissions.public_repos.description", (Object)"scopes.oauth2.permissions.account_write.description", (Object)"scopes.oauth2.permissions.repo_read.primary.description", (Object)"scopes.oauth2.permissions.repo_read.secondary.description", (Object)"scopes.oauth2.permissions.repo_write.description", (Object)"scopes.oauth2.permissions.repo_admin.primary.description", (Object)"scopes.oauth2.permissions.repo_admin.secondary.description", (Object)"scopes.oauth2.permissions.project_admin.primary.description", (Object)"scopes.oauth2.permissions.project_admin.secondary.description", (Object)"scopes.oauth2.permissions.admin_write.description", (Object)"scopes.oauth2.permissions.system_admin.description");
        i18nScopeKeys.forEach(key -> i18nStrings.put(key, this.i18nResolver.getText(key)));
        return ImmutableMap.copyOf(i18nStrings);
    }
}

