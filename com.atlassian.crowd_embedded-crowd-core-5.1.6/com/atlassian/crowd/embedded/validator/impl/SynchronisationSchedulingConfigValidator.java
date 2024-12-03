/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.util.I18nHelper
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.crowd.embedded.validator.impl;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.validator.DirectoryValidator;
import com.atlassian.crowd.embedded.validator.ValidationRule;
import com.atlassian.crowd.embedded.validator.impl.CrowdCronExpressionValidator;
import com.atlassian.crowd.embedded.validator.rule.DirectoryRuleBuilder;
import com.atlassian.crowd.embedded.validator.rule.RuleBuilder;
import com.atlassian.crowd.util.I18nHelper;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class SynchronisationSchedulingConfigValidator
extends DirectoryValidator {
    public static final String CRON_EXPRESSION_FIELD = "cronExpression";
    public static final String POLLING_INTERVAL_FIELD = "pollingIntervalInMin";

    public SynchronisationSchedulingConfigValidator(I18nHelper i18nHelper) {
        super(i18nHelper);
    }

    @Override
    protected List<ValidationRule<Directory>> initializeValidators(I18nHelper i18nHelper) {
        return ImmutableList.of(DirectoryRuleBuilder.ruleFor(POLLING_INTERVAL_FIELD).check(DirectoryRuleBuilder.valueOf("directory.cache.synchronise.type"), RuleBuilder.not(RuleBuilder.eq(CRON_EXPRESSION_FIELD))).check(DirectoryRuleBuilder.valueOf("directory.cache.synchronise.interval"), RuleBuilder.not(RuleBuilder.greaterThanOrEquals(1L))).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directory.polling.interval.invalid")).build(), DirectoryRuleBuilder.ruleFor(CRON_EXPRESSION_FIELD).check(DirectoryRuleBuilder.valueOf("directory.cache.synchronise.type"), RuleBuilder.eq(CRON_EXPRESSION_FIELD)).check(DirectoryRuleBuilder.valueOf("directory.cache.synchronise.cron"), value -> !CrowdCronExpressionValidator.isValid(value)).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directory.polling.cron.invalid")).build());
    }
}

