/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugins.whitelist.ImmutableWhitelistRule
 *  com.atlassian.plugins.whitelist.WhitelistManager
 *  com.atlassian.plugins.whitelist.WhitelistRule
 *  com.atlassian.plugins.whitelist.WhitelistType
 *  com.atlassian.plugins.whitelist.events.WhitelistRuleAddedEvent
 *  com.atlassian.plugins.whitelist.events.WhitelistRuleChangedEvent
 *  com.atlassian.plugins.whitelist.events.WhitelistRuleRemovedEvent
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.plugins.whitelist.core;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugins.whitelist.ImmutableWhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistManager;
import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistType;
import com.atlassian.plugins.whitelist.core.ao.AoWhitelistRule;
import com.atlassian.plugins.whitelist.core.ao.AoWhitelistRuleDao;
import com.atlassian.plugins.whitelist.core.matcher.RegularExpressionMatcher;
import com.atlassian.plugins.whitelist.events.WhitelistRuleAddedEvent;
import com.atlassian.plugins.whitelist.events.WhitelistRuleChangedEvent;
import com.atlassian.plugins.whitelist.events.WhitelistRuleRemovedEvent;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;

class WhitelistManagerImpl
implements WhitelistManager {
    private final AoWhitelistRuleDao aoWhitelistRuleDao;
    private final TransactionTemplate transactionTemplate;
    private final EventPublisher eventPublisher;

    public WhitelistManagerImpl(AoWhitelistRuleDao aoWhitelistRuleDao, TransactionTemplate transactionTemplate, EventPublisher eventPublisher) {
        this.aoWhitelistRuleDao = aoWhitelistRuleDao;
        this.transactionTemplate = transactionTemplate;
        this.eventPublisher = eventPublisher;
    }

    public WhitelistRule add(WhitelistRule whitelistRule) {
        return this.addAll(Collections.singleton(whitelistRule)).stream().findFirst().orElse(null);
    }

    public Collection<WhitelistRule> addAll(Iterable<WhitelistRule> whitelistRules) {
        Objects.requireNonNull(whitelistRules, "whitelistRules");
        Collection addedWhitelistRules = (Collection)this.transactionTemplate.execute(() -> this.addRules(whitelistRules));
        for (WhitelistRule whitelistRule : addedWhitelistRules) {
            this.eventPublisher.publish((Object)new WhitelistRuleAddedEvent(whitelistRule));
        }
        return addedWhitelistRules;
    }

    public WhitelistRule update(WhitelistRule whitelistRule) {
        Objects.requireNonNull(whitelistRule, "whitelistRule");
        Integer whitelistRuleId = whitelistRule.getId();
        Validate.isTrue((whitelistRuleId != null ? 1 : 0) != 0, (String)("Cannot update whitelist rule, the given instance has no database id: " + whitelistRule), (Object[])new Object[0]);
        WhitelistManagerImpl.validate(whitelistRule);
        WhitelistRuleUpdate update = (WhitelistRuleUpdate)this.transactionTemplate.execute(() -> {
            AoWhitelistRule aoWhitelistRuleData = this.aoWhitelistRuleDao.get(whitelistRuleId);
            ImmutableWhitelistRule oldRuleCopy = new ImmutableWhitelistRule((WhitelistRule)aoWhitelistRuleData);
            aoWhitelistRuleData.setExpression(whitelistRule.getExpression());
            aoWhitelistRuleData.setType(whitelistRule.getType());
            aoWhitelistRuleData.setAllowInbound(whitelistRule.isAllowInbound());
            aoWhitelistRuleData.setAuthenticationRequired(whitelistRule.isAuthenticationRequired());
            aoWhitelistRuleData.save();
            return new WhitelistRuleUpdate((WhitelistRule)oldRuleCopy, (WhitelistRule)new ImmutableWhitelistRule((WhitelistRule)aoWhitelistRuleData));
        });
        this.eventPublisher.publish((Object)new WhitelistRuleChangedEvent(update.oldRule, update.newRule));
        return update.newRule;
    }

    public void remove(WhitelistRule whitelistRule) {
        this.removeAll(Collections.singleton(whitelistRule));
    }

    public void removeAll(Iterable<WhitelistRule> whitelistRules) {
        Objects.requireNonNull(whitelistRules, "whitelistRules");
        Collection removedWhitelistRules = (Collection)this.transactionTemplate.execute(() -> this.removeRules(whitelistRules));
        for (WhitelistRule removedWhitelistRule : removedWhitelistRules) {
            this.eventPublisher.publish((Object)new WhitelistRuleRemovedEvent(removedWhitelistRule));
        }
    }

    public Collection<WhitelistRule> getAll() {
        return (Collection)this.transactionTemplate.execute(() -> ImmutableList.copyOf(this.aoWhitelistRuleDao.getAll()));
    }

    @Nullable
    public WhitelistRule get(int id) {
        return (WhitelistRule)this.transactionTemplate.execute(() -> this.aoWhitelistRuleDao.get(id));
    }

    private Collection<WhitelistRule> addRules(Iterable<WhitelistRule> rules) {
        return StreamSupport.stream(rules.spliterator(), false).map(rule -> {
            Objects.requireNonNull(rule, "rule");
            WhitelistManagerImpl.validate(rule);
            return this.aoWhitelistRuleDao.add((WhitelistRule)rule);
        }).collect(Collectors.toList());
    }

    private Collection<WhitelistRule> removeRules(Iterable<WhitelistRule> rules) {
        return ImmutableList.copyOf((Collection)StreamSupport.stream(rules.spliterator(), false).map(whitelistRule -> {
            Objects.requireNonNull(whitelistRule);
            Integer id = whitelistRule.getId();
            Validate.isTrue((id != null ? 1 : 0) != 0, (String)("Cannot remove whitelist rule, the given instance has no database id: " + whitelistRule), (Object[])new Object[0]);
            AoWhitelistRule aoWhitelistRuleData = this.aoWhitelistRuleDao.get(id);
            if (aoWhitelistRuleData == null) {
                return null;
            }
            this.aoWhitelistRuleDao.remove(id);
            return aoWhitelistRuleData;
        }).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    private static void validate(WhitelistRule whitelistRule) {
        if (whitelistRule.getType() == WhitelistType.REGULAR_EXPRESSION) {
            new RegularExpressionMatcher(whitelistRule.getExpression());
        } else if (whitelistRule.getType() == WhitelistType.APPLICATION_LINK) {
            try {
                UUID.fromString(whitelistRule.getExpression());
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Expected whitelist rule of type application link to have an expression of a valid UUID: " + whitelistRule);
            }
        }
    }

    private static class WhitelistRuleUpdate {
        @Nonnull
        private final WhitelistRule oldRule;
        @Nonnull
        private final WhitelistRule newRule;

        public WhitelistRuleUpdate(@Nonnull WhitelistRule oldRule, @Nonnull WhitelistRule newRule) {
            this.oldRule = oldRule;
            this.newRule = newRule;
        }
    }
}

