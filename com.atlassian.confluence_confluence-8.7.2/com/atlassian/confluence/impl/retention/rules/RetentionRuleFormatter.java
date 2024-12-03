/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.retention.AgeUnit
 *  com.atlassian.confluence.api.model.retention.GlobalRetentionPolicy
 *  com.atlassian.confluence.api.model.retention.RetentionRule
 *  com.atlassian.confluence.api.model.retention.TrashRetentionRule
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.confluence.impl.retention.rules;

import com.atlassian.confluence.api.model.retention.AgeUnit;
import com.atlassian.confluence.api.model.retention.GlobalRetentionPolicy;
import com.atlassian.confluence.api.model.retention.RetentionRule;
import com.atlassian.confluence.api.model.retention.TrashRetentionRule;
import com.atlassian.sal.api.message.I18nResolver;

public class RetentionRuleFormatter {
    private static final String EMPTY_SUMMARY = "";
    public static final String AND = "audit.logging.summary.global.retention.rules.and";
    public static final String KEEP_ALL = "audit.logging.summary.global.retention.rules.keep.all";
    public static final String VERSION = "audit.logging.summary.global.retention.rules.version";
    public static final String VERSIONS = "audit.logging.summary.global.retention.rules.versions";
    public static final String SPACE_OVERRIDES_ALLOWED = "audit.logging.summary.global.retention.rules.space.overrides.allowed";
    public static final String SPACE_OVERRIDES_NOT_ALLOWED = "audit.logging.summary.global.retention.rules.space.overrides.not.allowed";
    private final I18nResolver i18nResolver;

    public RetentionRuleFormatter(I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    public String format(RetentionRule rules) {
        if (rules == null) {
            return EMPTY_SUMMARY;
        }
        if (rules.getKeepAll()) {
            return this.i18nResolver.getText(KEEP_ALL);
        }
        if (rules.getMaxAge() != null) {
            return this.getAgeRulesSummary(rules);
        }
        if (rules.getMaxNumberOfVersions() != null) {
            return this.getNumberRulesSummary(rules);
        }
        return EMPTY_SUMMARY;
    }

    public String format(TrashRetentionRule rule) {
        if (rule == null) {
            return EMPTY_SUMMARY;
        }
        if (rule.getKeepAll()) {
            return this.i18nResolver.getText(KEEP_ALL);
        }
        if (rule.hasDeletedAgeLimit()) {
            return this.getAgeRuleSummary(rule.getMaxDeletedAge(), rule.getDeletedAgeUnit());
        }
        return EMPTY_SUMMARY;
    }

    public String format(GlobalRetentionPolicy globalRetentionPolicy) {
        return globalRetentionPolicy.getSpaceOverridesAllowed() ? this.i18nResolver.getText(SPACE_OVERRIDES_ALLOWED) : this.i18nResolver.getText(SPACE_OVERRIDES_NOT_ALLOWED);
    }

    private String getAgeUnitLabel(RetentionRule rules) {
        return rules.getMaxAge() == 1 ? rules.getAgeUnit().getSingleLabel() : rules.getAgeUnit().getPluralLabel();
    }

    private String getAgeRulesSummary(RetentionRule rules) {
        return String.format("%s %s", rules.getMaxAge(), this.i18nResolver.getText(this.getAgeUnitLabel(rules)));
    }

    private String getAgeRuleSummary(int maxAge, AgeUnit ageUnit) {
        return String.format("%s %s", maxAge, this.i18nResolver.getText(maxAge == 1 ? ageUnit.getSingleLabel() : ageUnit.getPluralLabel()));
    }

    private String getNumberRulesSummary(RetentionRule rules) {
        return String.format("%s %s", rules.getMaxNumberOfVersions(), this.getVersions(rules.getMaxNumberOfVersions()));
    }

    private String getVersions(int maxNumberOfVersions) {
        return maxNumberOfVersions > 1 ? this.i18nResolver.getText(VERSIONS) : this.i18nResolver.getText(VERSION);
    }
}

