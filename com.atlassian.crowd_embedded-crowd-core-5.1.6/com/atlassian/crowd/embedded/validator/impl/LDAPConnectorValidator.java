/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.util.I18nHelper
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package com.atlassian.crowd.embedded.validator.impl;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.validator.DirectoryValidator;
import com.atlassian.crowd.embedded.validator.ValidationRule;
import com.atlassian.crowd.embedded.validator.rule.DirectoryRuleBuilder;
import com.atlassian.crowd.embedded.validator.rule.RuleBuilder;
import com.atlassian.crowd.util.I18nHelper;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class LDAPConnectorValidator
extends DirectoryValidator {
    public static final String PAGED_RESULTS_SIZE_KEY = "pagedResultsSize";
    public static final String POLLING_INTERVAL_IN_MIN_KEY = "pollingIntervalInMin";
    public static final String LOCAL_USER_STATUS_KEY = "localUserStatusEnabled";
    public static final String LOCAL_GROUPS_ENABLED = "localGroupsEnabled";

    public LDAPConnectorValidator(I18nHelper i18nHelper) {
        super(i18nHelper);
    }

    @Override
    protected List<ValidationRule<Directory>> initializeValidators(I18nHelper i18nHelper) {
        ImmutableList.Builder ruleListBuilder = ImmutableList.builder();
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(PAGED_RESULTS_SIZE_KEY).check(DirectoryRuleBuilder.valueOf("ldap.pagedresults"), RuleBuilder.eq(Boolean.TRUE.toString())).check(DirectoryRuleBuilder.valueOf("ldap.pagedresults.size"), RuleBuilder.not(RuleBuilder.greaterThanOrEquals(100L))).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryconnector.pagedresultscontrolsize.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(LOCAL_USER_STATUS_KEY).check(DirectoryRuleBuilder.valueOf(LOCAL_USER_STATUS_KEY), RuleBuilder.eq(Boolean.TRUE.toString())).check(DirectoryRuleBuilder.valueOf("com.atlassian.crowd.directory.sync.cache.enabled"), RuleBuilder.eq(Boolean.FALSE.toString())).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryconnector.localuserstatus.withoutcache.message")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(LOCAL_GROUPS_ENABLED).check(DirectoryRuleBuilder.valueOf("ldap.local.groups"), RuleBuilder.eq(Boolean.TRUE.toString())).check(DirectoryRuleBuilder.valueOf("com.atlassian.crowd.directory.sync.cache.enabled"), RuleBuilder.eq(Boolean.FALSE.toString())).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryconnector.localgroups.withoutcache.message")).build());
        return ruleListBuilder.build();
    }
}

