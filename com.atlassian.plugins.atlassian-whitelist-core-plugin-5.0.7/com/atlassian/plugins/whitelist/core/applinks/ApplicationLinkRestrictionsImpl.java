/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Supplier
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugins.whitelist.ImmutableWhitelistRule
 *  com.atlassian.plugins.whitelist.WhitelistManager
 *  com.atlassian.plugins.whitelist.WhitelistRule
 *  com.atlassian.plugins.whitelist.WhitelistType
 *  com.atlassian.plugins.whitelist.applinks.ApplicationLinkRestrictions
 *  com.atlassian.plugins.whitelist.applinks.ApplicationLinkRestrictiveness
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.whitelist.core.applinks;

import com.atlassian.cache.Supplier;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugins.whitelist.ImmutableWhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistManager;
import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistType;
import com.atlassian.plugins.whitelist.applinks.ApplicationLinkRestrictions;
import com.atlassian.plugins.whitelist.applinks.ApplicationLinkRestrictiveness;
import com.atlassian.plugins.whitelist.core.applinks.ApplicationLinkRestrictionsCache;
import com.atlassian.plugins.whitelist.core.applinks.ApplicationLinkRestrictivenessChangeEvent;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationLinkRestrictionsImpl
implements ApplicationLinkRestrictions {
    private static final ApplicationLinkRestrictiveness DEFAULT_RESTRICTIVENESS = ApplicationLinkRestrictiveness.ALLOW_AUTHENTICATED;
    private static final String RESTRICTIVENESS_KEY = "com.atlassian.plugins.atlassian-whitelist-core-plugin:whitelist.applinks.restrictiveness";
    private static final Logger logger = LoggerFactory.getLogger(ApplicationLinkRestrictionsImpl.class);
    private final EventPublisher eventPublisher;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final ApplicationLinkRestrictionsCache cache;
    private final TransactionTemplate transactionTemplate;
    private final WhitelistManager whitelistManager;

    public ApplicationLinkRestrictionsImpl(PluginSettingsFactory pluginSettingsFactory, EventPublisher eventPublisher, ApplicationLinkRestrictionsCache cache, WhitelistManager whitelistManager, TransactionTemplate transactionTemplate) {
        this.pluginSettingsFactory = Objects.requireNonNull(pluginSettingsFactory);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.cache = Objects.requireNonNull(cache);
        this.whitelistManager = Objects.requireNonNull(whitelistManager);
        this.transactionTemplate = Objects.requireNonNull(transactionTemplate);
    }

    public void setRestrictiveness(ApplicationLinkRestrictiveness applicationLinkRestrictiveness) {
        this.updateRules(applicationLinkRestrictiveness);
        this.settings().put(RESTRICTIVENESS_KEY, (Object)applicationLinkRestrictiveness.name());
        this.cache.clear();
        logger.info("Application Link restrictiveness on the allowlist has been set to: {}", (Object)applicationLinkRestrictiveness.toString());
        this.eventPublisher.publish((Object)ApplicationLinkRestrictivenessChangeEvent.getInstance());
    }

    public ApplicationLinkRestrictiveness getRestrictiveness() {
        return this.cache.getWithDefault(RESTRICTIVENESS_KEY, (Supplier<ApplicationLinkRestrictiveness>)((Supplier)this::loadValueWithDefault));
    }

    private ApplicationLinkRestrictiveness loadValueWithDefault() {
        return Optional.ofNullable((String)this.settings().get(RESTRICTIVENESS_KEY)).map(ApplicationLinkRestrictiveness::valueOf).orElse(DEFAULT_RESTRICTIVENESS);
    }

    private PluginSettings settings() {
        return this.pluginSettingsFactory.createGlobalSettings();
    }

    private void updateRules(ApplicationLinkRestrictiveness restrictiveness) {
        if (restrictiveness.createApplinkRules()) {
            this.updateAllRulesAuthRequired(!restrictiveness.allowAnonymous());
        } else {
            this.removeAllRules();
        }
    }

    private void removeAllRules() {
        this.transactionTemplate.execute(() -> {
            this.getAllAppLinkRules().forEach(arg_0 -> ((WhitelistManager)this.whitelistManager).remove(arg_0));
            return null;
        });
    }

    private void updateAllRulesAuthRequired(boolean authRequired) {
        this.transactionTemplate.execute(() -> {
            this.getAllAppLinkRules().forEach(rule -> this.updateRuleAuthRequired((WhitelistRule)rule, authRequired));
            return null;
        });
    }

    private void updateRuleAuthRequired(WhitelistRule rule, boolean authRequired) {
        ImmutableWhitelistRule updatedRule = ImmutableWhitelistRule.builder().copyOf(rule).authenticationRequired(authRequired).build();
        this.whitelistManager.update((WhitelistRule)updatedRule);
    }

    private Collection<WhitelistRule> getAllAppLinkRules() {
        return this.whitelistManager.getAll().stream().filter(rule -> WhitelistType.APPLICATION_LINK.equals((Object)rule.getType())).collect(Collectors.toList());
    }
}

