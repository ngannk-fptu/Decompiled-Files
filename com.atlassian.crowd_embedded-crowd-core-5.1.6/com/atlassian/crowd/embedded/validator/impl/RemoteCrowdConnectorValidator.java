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

public class RemoteCrowdConnectorValidator
extends DirectoryValidator {
    public static final String POLLING_INTERVAL_IN_MIN = "pollingIntervalInMin";
    public static final String CROWD_HTTP_MAX_CONNECTIONS = "httpMaxConnections";

    public RemoteCrowdConnectorValidator(I18nHelper i18nHelper) {
        super(i18nHelper);
    }

    @Override
    protected List<ValidationRule<Directory>> initializeValidators(I18nHelper i18nHelper) {
        ImmutableList.Builder ruleListBuilder = ImmutableList.builder();
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(CROWD_HTTP_MAX_CONNECTIONS).check(DirectoryRuleBuilder.valueOf("crowd.server.http.max.connections"), RuleBuilder.isNull().or(RuleBuilder.not(RuleBuilder.greaterThanOrEquals(0L)))).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directorycrowd.http.maxconnections.invalid")).build());
        return ruleListBuilder.build();
    }
}

