/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.confluence.audit.AuditingContext
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.whitelist.WhitelistRule
 *  com.atlassian.plugins.whitelist.events.WhitelistDisabledEvent
 *  com.atlassian.plugins.whitelist.events.WhitelistEnabledEvent
 *  com.atlassian.plugins.whitelist.events.WhitelistRuleAddedEvent
 *  com.atlassian.plugins.whitelist.events.WhitelistRuleChangedEvent
 *  com.atlassian.plugins.whitelist.events.WhitelistRuleRemovedEvent
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.google.common.collect.ImmutableList
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.auditing.listeners;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.plugins.auditing.listeners.AbstractEventListener;
import com.atlassian.confluence.plugins.auditing.utils.AuditCategories;
import com.atlassian.confluence.plugins.auditing.utils.MessageKeyBuilder;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.events.WhitelistDisabledEvent;
import com.atlassian.plugins.whitelist.events.WhitelistEnabledEvent;
import com.atlassian.plugins.whitelist.events.WhitelistRuleAddedEvent;
import com.atlassian.plugins.whitelist.events.WhitelistRuleChangedEvent;
import com.atlassian.plugins.whitelist.events.WhitelistRuleRemovedEvent;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="whitelistConfigurationEventListener")
public class WhitelistConfigurationEventListener
extends AbstractEventListener {
    private static final String WHITELIST_TURNED_ON = MessageKeyBuilder.buildSummaryTextKey("whitelist.turned.on");
    private static final String WHITELIST_TURNED_OFF = MessageKeyBuilder.buildSummaryTextKey("whitelist.turned.off");
    private static final String WHITELIST_ITEM_ADDED = MessageKeyBuilder.buildSummaryTextKey("whitelist.url.added");
    private static final String WHITELIST_ITEM_UPDATED = MessageKeyBuilder.buildSummaryTextKey("whitelist.url.updated");
    private static final String WHITELIST_ITEM_REMOVED = MessageKeyBuilder.buildSummaryTextKey("whitelist.url.removed");
    private static final String WHITELIST_CHANGED_VALUE_TYPE = MessageKeyBuilder.buildChangedValueTextKey("whitelist.url.type");
    private static final String WHITELIST_CHANGED_VALUE_EXPRESSION = MessageKeyBuilder.buildChangedValueTextKey("whitelist.url.expression");
    private static final String WHITELIST_CHANGED_VALUE_ALLOW_INCOMING = MessageKeyBuilder.buildChangedValueTextKey("whitelist.url.allow.incoming");

    @Autowired
    public WhitelistConfigurationEventListener(@ComponentImport AuditService auditBroker, @ComponentImport(value="eventPublisher") EventListenerRegistrar eventListenerRegistrar, @ComponentImport I18nResolver i18nResolver, @ComponentImport LocaleResolver localeResolver, @ComponentImport AuditingContext auditingContext) {
        super(auditBroker, eventListenerRegistrar, i18nResolver, localeResolver, auditingContext);
    }

    @EventListener
    public void onWhitelistTurnedOn(WhitelistEnabledEvent event) {
        this.save(() -> AuditEvent.builder((AuditType)this.getAuditType(WHITELIST_TURNED_ON)).build());
    }

    @EventListener
    public void onWhitelistTurnedOff(WhitelistDisabledEvent event) {
        this.save(() -> AuditEvent.builder((AuditType)this.getAuditType(WHITELIST_TURNED_OFF)).build());
    }

    @EventListener
    public void onWhitelistRuleAdded(WhitelistRuleAddedEvent event) {
        this.save(() -> AuditEvent.builder((AuditType)this.getAuditType(WHITELIST_ITEM_ADDED)).changedValues(this.extractChangedValues(event)).build());
    }

    @EventListener
    public void onWhitelistRuleUpdated(WhitelistRuleChangedEvent event) {
        this.save(() -> AuditEvent.builder((AuditType)this.getAuditType(WHITELIST_ITEM_UPDATED)).changedValues(this.extractChangedValues(event)).build());
    }

    @EventListener
    public void onWhitelistRuleRemoved(WhitelistRuleRemovedEvent event) {
        this.save(() -> AuditEvent.builder((AuditType)this.getAuditType(WHITELIST_ITEM_REMOVED)).changedValues(this.extractChangedValues(event)).build());
    }

    private List<ChangedValue> extractChangedValues(WhitelistRuleAddedEvent event) {
        return ImmutableList.of((Object)ChangedValue.fromI18nKeys((String)WHITELIST_CHANGED_VALUE_TYPE).to(event.getWhitelistRule().getType().toString()).build(), (Object)ChangedValue.fromI18nKeys((String)WHITELIST_CHANGED_VALUE_EXPRESSION).to(event.getWhitelistRule().getExpression()).build(), (Object)ChangedValue.fromI18nKeys((String)WHITELIST_CHANGED_VALUE_ALLOW_INCOMING).to(String.valueOf(event.getWhitelistRule().isAllowInbound())).build());
    }

    private List<ChangedValue> extractChangedValues(WhitelistRuleChangedEvent event) {
        return Stream.of(ChangedValue.fromI18nKeys((String)WHITELIST_CHANGED_VALUE_TYPE).from((String)Optional.ofNullable(event.getOldRule()).map(WhitelistRule::getType).map(Enum::toString).orElse(null)).to(event.getNewRule().getType().toString()).build(), ChangedValue.fromI18nKeys((String)WHITELIST_CHANGED_VALUE_EXPRESSION).from((String)Optional.ofNullable(event.getOldRule()).map(WhitelistRule::getExpression).orElse(null)).to(event.getNewRule().getExpression()).build(), ChangedValue.fromI18nKeys((String)WHITELIST_CHANGED_VALUE_ALLOW_INCOMING).from((String)Optional.ofNullable(event.getOldRule()).map(WhitelistRule::isAllowInbound).map(String::valueOf).orElse(null)).to(String.valueOf(event.getNewRule().isAllowInbound())).build()).filter(cv -> !Objects.equals(cv.getFrom(), cv.getTo())).collect(Collectors.toList());
    }

    private List<ChangedValue> extractChangedValues(WhitelistRuleRemovedEvent event) {
        return ImmutableList.of((Object)ChangedValue.fromI18nKeys((String)WHITELIST_CHANGED_VALUE_TYPE).from(event.getWhitelistRule().getType().toString()).build(), (Object)ChangedValue.fromI18nKeys((String)WHITELIST_CHANGED_VALUE_EXPRESSION).from(event.getWhitelistRule().getExpression()).build(), (Object)ChangedValue.fromI18nKeys((String)WHITELIST_CHANGED_VALUE_ALLOW_INCOMING).from(String.valueOf(event.getWhitelistRule().isAllowInbound())).build());
    }

    private AuditType getAuditType(String actionKey) {
        return AuditType.fromI18nKeys((CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION, (CoverageLevel)CoverageLevel.ADVANCED, (String)AuditCategories.ADMIN_CATEGORY, (String)actionKey).build();
    }
}

