/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.Scope
 *  com.atlassian.oauth2.scopes.api.ScopeDescriptionService
 *  com.atlassian.oauth2.scopes.api.ScopeDescriptionWithTitle
 *  com.atlassian.oauth2.scopes.api.ScopeResolver
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.collect.ImmutableMap
 *  io.atlassian.util.concurrent.LazyReference
 */
package com.atlassian.oauth2.scopes.request;

import com.atlassian.oauth2.scopes.api.Scope;
import com.atlassian.oauth2.scopes.api.ScopeDescriptionService;
import com.atlassian.oauth2.scopes.api.ScopeDescriptionWithTitle;
import com.atlassian.oauth2.scopes.api.ScopeResolver;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.collect.ImmutableMap;
import io.atlassian.util.concurrent.LazyReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractScopeDescriptionService
implements ScopeDescriptionService {
    private final I18nResolver i18nResolver;
    private final ApplicationProperties applicationProperties;
    private final ScopeResolver scopeResolver;
    final LazyReference<Map<String, String>> detailedScopeDescriptions = new LazyReference(){

        protected Map<String, String> create() {
            return AbstractScopeDescriptionService.this.getDetailedScopeI18nStrings();
        }
    };

    protected AbstractScopeDescriptionService(I18nResolver i18nResolver, ApplicationProperties applicationProperties, ScopeResolver scopeResolver) {
        this.i18nResolver = i18nResolver;
        this.applicationProperties = applicationProperties;
        this.scopeResolver = scopeResolver;
    }

    public <V> Map<String, V> getScopeDescriptions(Function<String, V> getDescriptions) {
        Map<String, V> descriptions = Arrays.stream(this.getScopes()).map(Scope::getName).collect(Collectors.toMap(scopeName -> scopeName, getDescriptions));
        return ImmutableMap.copyOf(descriptions);
    }

    protected Map<String, String> getDetailedScopeI18nStrings() {
        Map detailedI18nString = (Map)Arrays.stream(this.getScopes()).map(scope -> scope.getName().toLowerCase()).map(scope -> {
            HashMap<String, String> i18nString = new HashMap<String, String>();
            String i18nKey = this.getI18nKey() + this.getProduct() + "." + scope + ".key";
            String i18nDescription = this.getI18nKey() + this.getProduct() + "." + scope + ".description";
            i18nString.put(i18nKey, this.i18nResolver.getText(i18nKey));
            i18nString.put(i18nDescription, this.i18nResolver.getText(i18nDescription));
            return i18nString;
        }).reduce(this.combineToSingleMap()).orElse((Map)ImmutableMap.copyOf(Collections.emptyMap()));
        return ImmutableMap.copyOf((Map)detailedI18nString);
    }

    protected ScopeDescriptionWithTitle getDescriptionsAndTitleFilteredByScope(String scope) {
        List descriptions = ((Map)Objects.requireNonNull(this.detailedScopeDescriptions.get())).entrySet().stream().filter(entry -> this.i18nKeyContainsScope((String)entry.getKey(), scope.toLowerCase())).map(Map.Entry::getValue).sorted(Comparator.comparing(String::length)).collect(Collectors.toList());
        return new ScopeDescriptionWithTitle((String)descriptions.get(0), (String)descriptions.get(1));
    }

    private boolean i18nKeyContainsScope(String i18nKey, String scope) {
        return i18nKey.contains(".".concat(scope));
    }

    protected <K, V> BinaryOperator<Map<K, V>> combineToSingleMap() {
        return (map, map2) -> {
            map.putAll(map2);
            return map;
        };
    }

    protected Scope[] getScopes() {
        return this.scopeResolver.getAvailableScopes().toArray(new Scope[0]);
    }

    protected abstract String getI18nKey();

    private String getProduct() {
        return this.applicationProperties.getDisplayName().toLowerCase();
    }
}

