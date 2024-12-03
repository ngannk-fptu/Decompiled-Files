/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.ScopeDescriptionService
 *  com.atlassian.oauth2.scopes.api.ScopeResolver
 *  com.atlassian.oauth2.scopes.api.ScopesRequestCache
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.oauth2.scopes.config;

import com.atlassian.oauth2.scopes.api.ScopeDescriptionService;
import com.atlassian.oauth2.scopes.api.ScopeResolver;
import com.atlassian.oauth2.scopes.api.ScopesRequestCache;
import com.atlassian.oauth2.scopes.config.AlwaysAllowedScopesCondition;
import com.atlassian.oauth2.scopes.config.BasicScopeCondition;
import com.atlassian.oauth2.scopes.request.DefaultScopeResolver;
import com.atlassian.oauth2.scopes.request.bamboo.BambooScope;
import com.atlassian.oauth2.scopes.request.bamboo.BambooScopesRequestCache;
import com.atlassian.oauth2.scopes.request.basic.BasicScope;
import com.atlassian.oauth2.scopes.request.basic.BasicScopeDescriptionService;
import com.atlassian.oauth2.scopes.request.basic.ConfluenceScopesRequestCache;
import com.atlassian.oauth2.scopes.request.basic.JiraScopesRequestCache;
import com.atlassian.oauth2.scopes.request.bitbucket.BitbucketScope;
import com.atlassian.oauth2.scopes.request.bitbucket.BitbucketScopeDescriptionService;
import com.atlassian.oauth2.scopes.request.bitbucket.BitbucketScopesRequestCache;
import com.atlassian.oauth2.scopes.request.empty.EmptyScopeDescriptionService;
import com.atlassian.oauth2.scopes.request.empty.EmptyScopeResolver;
import com.atlassian.oauth2.scopes.request.empty.EmptyScopesRequestCache;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.BambooOnly;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.BitbucketOnly;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.ConfluenceOnly;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.JiraOnly;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScopesPluginConfiguration {
    @Bean
    @Conditional(value={BasicScopeCondition.class})
    public ScopeResolver basicScopeResolver() {
        return new DefaultScopeResolver(Arrays.stream(BasicScope.values()).collect(Collectors.toSet()));
    }

    @Bean
    @Conditional(value={BitbucketOnly.class})
    public ScopeResolver bitbucketScopeResolver() {
        return new DefaultScopeResolver(Arrays.stream(BitbucketScope.values()).collect(Collectors.toSet()));
    }

    @Bean
    @Conditional(value={AlwaysAllowedScopesCondition.class})
    public ScopeResolver emptyScopeResolver() {
        return new EmptyScopeResolver();
    }

    @Bean
    @Conditional(value={BambooOnly.class})
    public ScopeResolver bambooScopeResolver() {
        return new DefaultScopeResolver(Arrays.stream(BambooScope.values()).collect(Collectors.toSet()));
    }

    @Bean
    @Conditional(value={JiraOnly.class})
    public ScopesRequestCache jiraScopesRequestCache(ScopeResolver scopeResolver) {
        return new JiraScopesRequestCache(scopeResolver);
    }

    @Bean
    @Conditional(value={ConfluenceOnly.class})
    public ScopesRequestCache confluenceScopesRequestCache(ScopeResolver scopeResolver) {
        return new ConfluenceScopesRequestCache(scopeResolver);
    }

    @Bean
    @Conditional(value={BitbucketOnly.class})
    public ScopesRequestCache bitbucketScopesRequestCache(ScopeResolver scopeResolver) {
        return new BitbucketScopesRequestCache(scopeResolver);
    }

    @Bean
    @Conditional(value={BambooOnly.class})
    public ScopesRequestCache bambooScopesRequestCache(ScopeResolver scopeResolver) {
        return new BambooScopesRequestCache(scopeResolver);
    }

    @Bean
    @Conditional(value={AlwaysAllowedScopesCondition.class})
    public ScopesRequestCache alwaysAllowedScopesRequestCache() {
        return new EmptyScopesRequestCache();
    }

    @Bean
    @Conditional(value={BasicScopeCondition.class})
    public ScopeDescriptionService basicScopeDescriptionService(I18nResolver i18nResolver, ApplicationProperties applicationProperties, ScopeResolver scopeResolver) {
        return new BasicScopeDescriptionService(i18nResolver, applicationProperties, scopeResolver);
    }

    @Bean
    @Conditional(value={BitbucketOnly.class})
    public ScopeDescriptionService bitbucketScopeDescriptionService(I18nResolver i18nResolver, ApplicationProperties applicationProperties, ScopeResolver scopeResolver) {
        return new BitbucketScopeDescriptionService(i18nResolver, applicationProperties, scopeResolver);
    }

    @Bean
    @Conditional(value={BambooOnly.class})
    public ScopeDescriptionService bambooScopeDescriptionService(I18nResolver i18nResolver, ApplicationProperties applicationProperties, ScopeResolver scopeResolver) {
        return new BasicScopeDescriptionService(i18nResolver, applicationProperties, scopeResolver);
    }

    @Bean
    @Conditional(value={AlwaysAllowedScopesCondition.class})
    public ScopeDescriptionService emptyScopeDescriptionService(I18nResolver i18nResolver, ApplicationProperties applicationProperties, ScopeResolver scopeResolver) {
        return new EmptyScopeDescriptionService(i18nResolver, applicationProperties, scopeResolver);
    }
}

