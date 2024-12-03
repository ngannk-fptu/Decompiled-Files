/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugins.whitelist.ImmutableWhitelistRuleBuilder
 *  com.atlassian.plugins.whitelist.WhitelistManager
 *  com.atlassian.plugins.whitelist.WhitelistOnOffSwitch
 *  com.atlassian.plugins.whitelist.WhitelistRule
 *  com.atlassian.plugins.whitelist.WhitelistService
 *  com.atlassian.plugins.whitelist.WhitelistType
 *  com.atlassian.plugins.whitelist.events.ClearWhitelistCacheEvent
 *  com.atlassian.plugins.whitelist.events.WhitelistRuleAddedEvent
 *  com.atlassian.plugins.whitelist.events.WhitelistRuleChangedEvent
 *  com.atlassian.plugins.whitelist.events.WhitelistRuleRemovedEvent
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.plugins.whitelist.core;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugins.whitelist.ImmutableWhitelistRuleBuilder;
import com.atlassian.plugins.whitelist.WhitelistManager;
import com.atlassian.plugins.whitelist.WhitelistOnOffSwitch;
import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistService;
import com.atlassian.plugins.whitelist.WhitelistType;
import com.atlassian.plugins.whitelist.core.WhitelistRuleNotFoundException;
import com.atlassian.plugins.whitelist.core.WhitelistRulePredicates;
import com.atlassian.plugins.whitelist.core.applinks.ApplicationLinkRestrictivenessChangeEvent;
import com.atlassian.plugins.whitelist.core.permission.PermissionChecker;
import com.atlassian.plugins.whitelist.events.ClearWhitelistCacheEvent;
import com.atlassian.plugins.whitelist.events.WhitelistRuleAddedEvent;
import com.atlassian.plugins.whitelist.events.WhitelistRuleChangedEvent;
import com.atlassian.plugins.whitelist.events.WhitelistRuleRemovedEvent;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class WhitelistServiceImpl
implements WhitelistService,
InitializingBean,
DisposableBean {
    private static final String CACHE_NAME = WhitelistServiceImpl.class.getName() + ".cache";
    private static final String CACHE_KEY = "5";
    private static final Logger logger = LoggerFactory.getLogger(WhitelistServiceImpl.class);
    private final WhitelistOnOffSwitch whitelistOnOffSwitch;
    private final WhitelistManager whitelistManager;
    private final PermissionChecker permissionChecker;
    private final EventPublisher eventPublisher;
    private final Cache<String, ImmutableList<WhitelistRule>> cache;

    public WhitelistServiceImpl(WhitelistOnOffSwitch whitelistOnOffSwitch, WhitelistManager whitelistManager, PermissionChecker permissionChecker, EventPublisher eventPublisher, CacheManager cacheFactory) {
        this.whitelistOnOffSwitch = whitelistOnOffSwitch;
        this.whitelistManager = whitelistManager;
        this.permissionChecker = permissionChecker;
        this.eventPublisher = eventPublisher;
        this.cache = cacheFactory.getCache(CACHE_NAME, null, new CacheSettingsBuilder().remote().expireAfterWrite(1L, TimeUnit.HOURS).build());
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    public boolean isWhitelistEnabled() {
        return this.whitelistOnOffSwitch.isEnabled();
    }

    public void enableWhitelist() {
        this.permissionChecker.checkCurrentUserCanManageWhitelist();
        this.whitelistOnOffSwitch.enable();
    }

    public void disableWhitelist() {
        this.permissionChecker.checkCurrentUserCanManageWhitelist();
        this.whitelistOnOffSwitch.disable();
    }

    public WhitelistRule add(WhitelistRule whitelistRule) {
        Objects.requireNonNull(whitelistRule, "whitelistRule");
        this.permissionChecker.checkCurrentUserCanManageWhitelist();
        WhitelistServiceImpl.checkTypeNotApplicationLink(whitelistRule);
        return this.whitelistManager.add(whitelistRule);
    }

    public WhitelistRule update(WhitelistRule whitelistRule) {
        Objects.requireNonNull(whitelistRule, "whitelistRule");
        Integer whitelistRuleId = whitelistRule.getId();
        Validate.isTrue((whitelistRuleId != null ? 1 : 0) != 0, (String)("Cannot update whitelist rule, the given instance has no database id: " + whitelistRule), (Object[])new Object[0]);
        this.permissionChecker.checkCurrentUserCanManageWhitelist();
        this.enforceApplicationLinkUpdateRules(whitelistRule);
        return this.whitelistManager.update(whitelistRule);
    }

    public void remove(int id) {
        this.permissionChecker.checkCurrentUserCanManageWhitelist();
        WhitelistRule whitelistRule = this.get(id);
        if (whitelistRule != null) {
            WhitelistServiceImpl.checkTypeNotApplicationLink(whitelistRule);
            this.whitelistManager.remove(whitelistRule);
        }
    }

    public Collection<WhitelistRule> getAll() {
        try {
            return (Collection)this.cache.get((Object)CACHE_KEY, this::loadAll);
        }
        catch (RuntimeException th) {
            logger.warn("Failed to read entry from cache '" + CACHE_NAME + "': {}", (Object)th.getMessage());
            return this.loadAll();
        }
    }

    @Nullable
    public WhitelistRule get(int id) {
        return this.getAll().stream().filter(WhitelistRulePredicates.withId(id)).findFirst().orElse(null);
    }

    @EventListener
    public void onWhitelistRuleAddedEvent(WhitelistRuleAddedEvent event) {
        this.clearCache();
    }

    @EventListener
    public void onWhitelistRuleChanged(WhitelistRuleChangedEvent event) {
        this.clearCache();
    }

    @EventListener
    public void onWhitelistRuleRemoved(WhitelistRuleRemovedEvent event) {
        this.clearCache();
    }

    @EventListener
    public void onClearWhitelistCacheEvent(ClearWhitelistCacheEvent event) {
        this.clearCache();
    }

    @EventListener
    public void onWhitelistRestrictivenessChange(ApplicationLinkRestrictivenessChangeEvent event) {
        this.clearCache();
    }

    private static void checkTypeNotApplicationLink(WhitelistRule whitelistRule) {
        if (whitelistRule.getType() == WhitelistType.APPLICATION_LINK) {
            throw new IllegalArgumentException("Adding new application link whitelist rules is not supported, they are managed internally.");
        }
    }

    private void clearCache() {
        try {
            this.cache.removeAll();
        }
        catch (RuntimeException th) {
            logger.error("Failed to remove all entries from cache '" + CACHE_NAME + "': {}", (Object)th.getMessage());
        }
    }

    private void enforceApplicationLinkUpdateRules(WhitelistRule whitelistRule) {
        Integer whitelistRuleId = whitelistRule.getId();
        Objects.requireNonNull(whitelistRuleId, "whitelistRuleId");
        WhitelistRule existingWhitelistRule = this.get(whitelistRuleId);
        if (existingWhitelistRule == null) {
            throw new WhitelistRuleNotFoundException("Whitelist rule with id '" + whitelistRuleId + "' not found.");
        }
        if (existingWhitelistRule.getType() == WhitelistType.APPLICATION_LINK) {
            Validate.isTrue((whitelistRule.getType() == WhitelistType.APPLICATION_LINK && whitelistRule.getExpression().equals(existingWhitelistRule.getExpression()) ? 1 : 0) != 0, (String)"Cannot change the type or expression of this application link whitelist rule.", (Object[])new Object[0]);
        } else {
            Validate.isTrue((whitelistRule.getType() != WhitelistType.APPLICATION_LINK ? 1 : 0) != 0, (String)"Cannot change the type to application link.", (Object[])new Object[0]);
        }
    }

    @Nonnull
    private ImmutableList<WhitelistRule> loadAll() {
        return ImmutableList.copyOf((Collection)this.whitelistManager.getAll().stream().map(ImmutableWhitelistRuleBuilder.COPY).collect(Collectors.toList()));
    }
}

