/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.applinks.api.event.ApplicationLinkAddedEvent
 *  com.atlassian.applinks.api.event.ApplicationLinkDeletedEvent
 *  com.atlassian.applinks.api.event.ApplicationLinksIDChangedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.events.PluginFrameworkStartedEvent
 *  com.atlassian.plugins.whitelist.ImmutableWhitelistRule
 *  com.atlassian.plugins.whitelist.WhitelistManager
 *  com.atlassian.plugins.whitelist.WhitelistRule
 *  com.atlassian.plugins.whitelist.WhitelistType
 *  com.atlassian.plugins.whitelist.applinks.ApplicationLinkRestrictions
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.plugins.whitelist.core.applinks;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.applinks.api.event.ApplicationLinkAddedEvent;
import com.atlassian.applinks.api.event.ApplicationLinkDeletedEvent;
import com.atlassian.applinks.api.event.ApplicationLinksIDChangedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.events.PluginFrameworkStartedEvent;
import com.atlassian.plugins.whitelist.ImmutableWhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistManager;
import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistType;
import com.atlassian.plugins.whitelist.applinks.ApplicationLinkRestrictions;
import com.atlassian.plugins.whitelist.core.WhitelistRulePredicates;
import com.atlassian.plugins.whitelist.core.applinks.ApplicationLinkRestrictivenessChangeEvent;
import com.atlassian.plugins.whitelist.core.applinks.ApplicationLinkWhitelistRule;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class ApplicationLinkChangeListener
implements InitializingBean,
DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationLinkChangeListener.class);
    private final EventPublisher eventPublisher;
    private final WhitelistManager whitelistManager;
    private final TransactionTemplate transactionTemplate;
    private final ReadOnlyApplicationLinkService applicationLinkService;
    private final ApplicationLinkRestrictions applicationLinkRestrictions;
    private boolean started;

    public ApplicationLinkChangeListener(EventPublisher eventPublisher, WhitelistManager whitelistManager, TransactionTemplate transactionTemplate, ReadOnlyApplicationLinkService applicationLinkService, ApplicationLinkRestrictions applicationLinkRestrictions) {
        this.eventPublisher = eventPublisher;
        this.whitelistManager = whitelistManager;
        this.transactionTemplate = transactionTemplate;
        this.applicationLinkService = applicationLinkService;
        this.applicationLinkRestrictions = applicationLinkRestrictions;
    }

    @EventListener
    public void onApplicationLinkAdded(ApplicationLinkAddedEvent event) {
        if (this.started && this.shouldCreateApplinks()) {
            this.add((ReadOnlyApplicationLink)event.getApplicationLink());
        }
    }

    @EventListener
    public void onApplicationLinkIdChanged(ApplicationLinksIDChangedEvent event) {
        if (this.started && this.shouldCreateApplinks()) {
            this.update(event.getOldApplicationId(), (ReadOnlyApplicationLink)event.getApplicationLink());
        }
    }

    @EventListener
    public void onApplicationLinkDeleted(ApplicationLinkDeletedEvent event) {
        if (this.started) {
            this.remove(event.getApplicationLink().getId());
        }
    }

    @EventListener
    public void onPluginFrameworkStarted(PluginFrameworkStartedEvent event) {
        this.refreshApplinkRules();
        this.started = true;
    }

    @EventListener
    public void onRestrictivenessChange(ApplicationLinkRestrictivenessChangeEvent event) {
        this.refreshApplinkRules();
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    private void add(ReadOnlyApplicationLink applicationLink) {
        this.transactionTemplate.execute(() -> {
            Collection<WhitelistRule> existingRules = this.findAllWhitelistedApplicationLinksWithId(applicationLink.getId());
            if (existingRules.isEmpty()) {
                this.whitelistManager.add((WhitelistRule)new ApplicationLinkWhitelistRule(applicationLink, !this.applicationLinkRestrictions.getRestrictiveness().allowAnonymous()));
            }
            return null;
        });
    }

    private void add(Iterable<ReadOnlyApplicationLink> applicationLinks) {
        this.transactionTemplate.execute(() -> {
            for (ReadOnlyApplicationLink applicationLink : applicationLinks) {
                this.whitelistManager.add((WhitelistRule)new ApplicationLinkWhitelistRule(applicationLink, !this.applicationLinkRestrictions.getRestrictiveness().allowAnonymous()));
            }
            return null;
        });
    }

    private void update(ApplicationId oldApplicationId, ReadOnlyApplicationLink applicationLink) {
        this.transactionTemplate.execute(() -> {
            LinkedList applinksWithOldApplicationId = Lists.newLinkedList(this.findAllWhitelistedApplicationLinksWithId(oldApplicationId));
            WhitelistRule first = (WhitelistRule)applinksWithOldApplicationId.pollFirst();
            if (first != null) {
                ImmutableWhitelistRule expression = ImmutableWhitelistRule.builder().copyOf(first).expression(ApplicationLinkWhitelistRule.getExpressionFrom(applicationLink)).build();
                this.whitelistManager.update((WhitelistRule)expression);
            }
            this.whitelistManager.removeAll((Iterable)applinksWithOldApplicationId);
            return null;
        });
    }

    private void remove(ApplicationId applicationId) {
        this.transactionTemplate.execute(() -> {
            Collection<WhitelistRule> applinksWithId = this.findAllWhitelistedApplicationLinksWithId(applicationId);
            this.whitelistManager.removeAll(applinksWithId);
            return null;
        });
    }

    private void refreshApplinkRules() {
        if (this.shouldCreateApplinks()) {
            Set whitelistedApplicationLinkIds = this.whitelistManager.getAll().stream().filter(WhitelistRulePredicates.withType(WhitelistType.APPLICATION_LINK)).map(WhitelistRule::getExpression).collect(Collectors.toSet());
            Iterable nonWhitelistedApplicationLinks = StreamSupport.stream(this.applicationLinkService.getApplicationLinks().spliterator(), false).filter(applicationLink -> !whitelistedApplicationLinkIds.contains(applicationLink.getId().get())).collect(Collectors.toList());
            this.add(nonWhitelistedApplicationLinks);
        } else {
            this.whitelistManager.getAll().stream().filter(WhitelistRulePredicates.withType(WhitelistType.APPLICATION_LINK)).forEach(arg_0 -> ((WhitelistManager)this.whitelistManager).remove(arg_0));
        }
    }

    private Collection<WhitelistRule> findAllWhitelistedApplicationLinksWithId(ApplicationId applicationId) {
        String expression = ApplicationLinkWhitelistRule.getExpressionFrom(applicationId);
        Collection result = this.whitelistManager.getAll().stream().filter(WhitelistRulePredicates.withType(WhitelistType.APPLICATION_LINK)).filter(WhitelistRulePredicates.withExpression(expression)).collect(Collectors.toList());
        if (result.size() > 1) {
            logger.warn("Found more than one whitelist entry for the application link with id '{}'.", (Object)applicationId);
        }
        return result;
    }

    private boolean shouldCreateApplinks() {
        return this.applicationLinkRestrictions.getRestrictiveness().createApplinkRules();
    }
}

