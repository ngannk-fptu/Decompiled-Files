/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditEvent$Builder
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.ChangedValue$Builder
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.confluence.api.model.retention.GlobalRetentionPolicy
 *  com.atlassian.confluence.api.model.retention.RetentionPolicy
 *  com.atlassian.confluence.api.model.retention.RetentionRule
 *  com.atlassian.confluence.api.model.retention.TrashRetentionRule
 *  com.atlassian.event.api.EventListener
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.retention.audit.listener;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.api.model.retention.GlobalRetentionPolicy;
import com.atlassian.confluence.api.model.retention.RetentionPolicy;
import com.atlassian.confluence.api.model.retention.RetentionRule;
import com.atlassian.confluence.api.model.retention.TrashRetentionRule;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.event.events.retention.GlobalRetentionPolicyChangedEvent;
import com.atlassian.confluence.event.events.retention.SpaceRetentionPolicyChangedEvent;
import com.atlassian.confluence.event.events.retention.SpaceRetentionPolicyCreatedEvent;
import com.atlassian.confluence.event.events.retention.SpaceRetentionPolicyDeletedEvent;
import com.atlassian.confluence.impl.retention.manager.GlobalRetentionPolicyManager;
import com.atlassian.confluence.impl.retention.rules.RetentionRuleFormatter;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.event.api.EventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetentionPolicyAuditListener {
    public static final String KEY = "audit.logging.category.admin";
    public static final String GLOBAL_RETENTION_POLICY_CHANGED_SUMMARY = "audit.logging.summary.global.retention.rules.updated";
    public static final String GLOBAL_RETENTION_POLICY_OVERRIDE_CHANGED = "audit.logging.summary.retention.rules.space.override.updated";
    public static final String RETENTION_POLICY_PAGE_RULE_CHANGED = "audit.logging.summary.retention.rules.page.updated";
    public static final String RETENTION_POLICY_ATTACHMENT_RULE_CHANGED = "audit.logging.summary.retention.rules.attachment.updated";
    public static final String RETENTION_POLICY_TRASH_RULE_CHANGED = "audit.logging.summary.retention.rules.trash.updated";
    public static final String SPACE_KEY = "audit.logging.summary.space.retention.rules.space.key";
    public static final String SPACE_RETENTION_POLICY_CREATED_SUMMARY = "audit.logging.summary.space.retention.rules.created";
    public static final String SPACE_RETENTION_POLICY_CHANGED_SUMMARY = "audit.logging.summary.space.retention.rules.updated";
    public static final String SPACE_RETENTION_POLICY_DELETED_SUMMARY = "audit.logging.summary.space.retention.rules.deleted";
    private final Logger logger = LoggerFactory.getLogger(RetentionPolicyAuditListener.class);
    private final AuditService auditService;
    private final RetentionRuleFormatter retentionRuleFormatter;
    private final StandardAuditResourceTypes resourceTypes;
    private final GlobalRetentionPolicyManager globalRetentionPolicyManager;

    public RetentionPolicyAuditListener(AuditService auditService, RetentionRuleFormatter retentionRuleFormatter, StandardAuditResourceTypes resourceTypes, GlobalRetentionPolicyManager globalRetentionPolicyManager) {
        this.auditService = auditService;
        this.retentionRuleFormatter = retentionRuleFormatter;
        this.resourceTypes = resourceTypes;
        this.globalRetentionPolicyManager = globalRetentionPolicyManager;
    }

    @EventListener
    public void onGlobalRetentionPolicyChanged(GlobalRetentionPolicyChangedEvent event) {
        this.logger.debug("GlobalRetentionPolicyChangedEvent received: {}", (Object)event);
        if (!event.getOldPolicy().equals(event.getNewPolicy())) {
            this.auditService.audit(this.buildGlobalPolicyAuditEvent(this.getChangedValuesForUpdate(event.getOldPolicy(), event.getNewPolicy())));
        }
    }

    @EventListener
    public void onSpaceRetentionPolicyCreated(SpaceRetentionPolicyCreatedEvent event) {
        this.logger.debug("SpaceRetentionPolicyCreatedEvent received: {}", (Object)event);
        this.auditService.audit(this.buildSpacePolicyAuditEvent(SPACE_RETENTION_POLICY_CREATED_SUMMARY, event.getSpace(), this.getChangedValuesForCreation(event.getNewPolicy())));
    }

    @EventListener
    public void onSpaceRetentionPolicyChanged(SpaceRetentionPolicyChangedEvent event) {
        this.logger.debug("SpaceRetentionPolicyChangedEvent received: {}", (Object)event);
        if (!event.getOldPolicy().equals(event.getNewPolicy())) {
            this.auditService.audit(this.buildSpacePolicyAuditEvent(SPACE_RETENTION_POLICY_CHANGED_SUMMARY, event.getSpace(), this.getChangedValuesForUpdate(event.getOldPolicy(), event.getNewPolicy())));
        }
    }

    @EventListener
    public void onSpaceRetentionPolicyDeleted(SpaceRetentionPolicyDeletedEvent event) {
        this.logger.debug("SpaceRetentionPolicyDeletedEvent received: {}", (Object)event);
        this.auditService.audit(this.buildSpacePolicyAuditEvent(SPACE_RETENTION_POLICY_DELETED_SUMMARY, event.getSpace(), this.getChangedValuesForDeletion(event.getOldPolicy())));
    }

    private AuditEvent buildGlobalPolicyAuditEvent(List<ChangedValue> changedValues) {
        return this.getBaseAuditEventBuilder(GLOBAL_RETENTION_POLICY_CHANGED_SUMMARY, changedValues).build();
    }

    private AuditEvent buildSpacePolicyAuditEvent(String summary, Space space, List<ChangedValue> changedValues) {
        return this.getBaseAuditEventBuilder(summary, changedValues).affectedObject(this.buildAuditResource(space.getName(), Long.toString(space.getId()))).build();
    }

    private AuditEvent.Builder getBaseAuditEventBuilder(String summary, List<ChangedValue> changedValues) {
        return AuditEvent.fromI18nKeys((String)KEY, (String)summary, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION).changedValues(changedValues);
    }

    private List<ChangedValue> getChangedValuesForUpdate(RetentionPolicy oldPolicy, RetentionPolicy newPolicy) {
        ArrayList<ChangedValue> changedValues = new ArrayList<ChangedValue>();
        if (!oldPolicy.getPageVersionRule().equals((Object)newPolicy.getPageVersionRule())) {
            changedValues.add(this.getChangedValue(RETENTION_POLICY_PAGE_RULE_CHANGED, oldPolicy.getPageVersionRule(), newPolicy.getPageVersionRule()));
        }
        if (!oldPolicy.getAttachmentRetentionRule().equals((Object)newPolicy.getAttachmentRetentionRule())) {
            changedValues.add(this.getChangedValue(RETENTION_POLICY_ATTACHMENT_RULE_CHANGED, oldPolicy.getAttachmentRetentionRule(), newPolicy.getAttachmentRetentionRule()));
        }
        if (!oldPolicy.getTrashRetentionRule().equals((Object)newPolicy.getTrashRetentionRule())) {
            changedValues.add(this.getChangedValueForTrashRule(oldPolicy.getTrashRetentionRule(), newPolicy.getTrashRetentionRule()));
        }
        if (this.hasSpaceOverrideChanged(oldPolicy, newPolicy)) {
            changedValues.add(this.getChangeValueForPolicy(oldPolicy, newPolicy));
        }
        return changedValues;
    }

    private boolean hasSpaceOverrideChanged(RetentionPolicy oldPolicy, RetentionPolicy newPolicy) {
        return oldPolicy instanceof GlobalRetentionPolicy && newPolicy instanceof GlobalRetentionPolicy && !((GlobalRetentionPolicy)oldPolicy).getSpaceOverridesAllowed() == ((GlobalRetentionPolicy)newPolicy).getSpaceOverridesAllowed();
    }

    private List<ChangedValue> getChangedValuesForCreation(RetentionPolicy newPolicy) {
        ArrayList<ChangedValue> changedValues = new ArrayList<ChangedValue>();
        GlobalRetentionPolicy currentGlobalPolicy = this.globalRetentionPolicyManager.getPolicy();
        changedValues.add(this.getChangedValue(RETENTION_POLICY_PAGE_RULE_CHANGED, currentGlobalPolicy.getPageVersionRule(), newPolicy.getPageVersionRule()));
        changedValues.add(this.getChangedValue(RETENTION_POLICY_ATTACHMENT_RULE_CHANGED, currentGlobalPolicy.getAttachmentRetentionRule(), newPolicy.getAttachmentRetentionRule()));
        changedValues.add(this.getChangedValueForTrashRule(currentGlobalPolicy.getTrashRetentionRule(), newPolicy.getTrashRetentionRule()));
        return changedValues;
    }

    private List<ChangedValue> getChangedValuesForDeletion(RetentionPolicy oldPolicy) {
        ArrayList<ChangedValue> changedValues = new ArrayList<ChangedValue>();
        GlobalRetentionPolicy currentGlobalPolicy = this.globalRetentionPolicyManager.getPolicy();
        changedValues.add(this.getChangedValue(RETENTION_POLICY_PAGE_RULE_CHANGED, oldPolicy.getPageVersionRule(), currentGlobalPolicy.getAttachmentRetentionRule()));
        changedValues.add(this.getChangedValue(RETENTION_POLICY_ATTACHMENT_RULE_CHANGED, oldPolicy.getAttachmentRetentionRule(), currentGlobalPolicy.getPageVersionRule()));
        changedValues.add(this.getChangedValueForTrashRule(oldPolicy.getTrashRetentionRule(), currentGlobalPolicy.getTrashRetentionRule()));
        return changedValues;
    }

    private ChangedValue getChangedValue(String key, RetentionRule oldRule, RetentionRule newRule) {
        ChangedValue.Builder ruleChangedValueBuilder = ChangedValue.fromI18nKeys((String)key);
        return ruleChangedValueBuilder.from(this.retentionRuleFormatter.format(oldRule)).to(this.retentionRuleFormatter.format(newRule)).build();
    }

    private ChangedValue getChangedValueForTrashRule(TrashRetentionRule oldRule, TrashRetentionRule newRule) {
        ChangedValue.Builder ruleChangedValueBuilder = ChangedValue.fromI18nKeys((String)RETENTION_POLICY_TRASH_RULE_CHANGED);
        return ruleChangedValueBuilder.from(this.retentionRuleFormatter.format(oldRule)).to(this.retentionRuleFormatter.format(newRule)).build();
    }

    private ChangedValue getChangeValueForPolicy(RetentionPolicy oldPolicy, RetentionPolicy newPolicy) {
        ChangedValue.Builder ruleChangedValueBuilder = ChangedValue.fromI18nKeys((String)GLOBAL_RETENTION_POLICY_OVERRIDE_CHANGED);
        return ruleChangedValueBuilder.from(this.retentionRuleFormatter.format((GlobalRetentionPolicy)oldPolicy)).to(this.retentionRuleFormatter.format((GlobalRetentionPolicy)newPolicy)).build();
    }

    private AuditResource buildAuditResource(String spaceName, @Nullable String spaceId) {
        return AuditResource.builder((String)Optional.ofNullable(spaceName).orElse("Undefined"), (String)this.resourceTypes.space()).id(spaceId).build();
    }
}

