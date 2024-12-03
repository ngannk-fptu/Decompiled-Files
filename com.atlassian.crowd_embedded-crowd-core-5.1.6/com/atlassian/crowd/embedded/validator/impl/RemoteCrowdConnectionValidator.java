/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.util.I18nHelper
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.apache.commons.lang3.StringUtils
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
import org.apache.commons.lang3.StringUtils;

public class RemoteCrowdConnectionValidator
extends DirectoryValidator {
    public static final String CROWD_SERVER_URL = "url";
    public static final String APPLICATION_NAME = "applicationName";
    public static final String APPLICATION_PASSWORD = "applicationPassword";
    public static final String CROWD_HTTP_PROXY_PORT = "httpProxyPort";
    public static final Long MAX_PORT_NUMBER = 65535L;

    public RemoteCrowdConnectionValidator(I18nHelper i18nHelper) {
        super(i18nHelper);
    }

    @Override
    protected List<ValidationRule<Directory>> initializeValidators(I18nHelper i18nHelper) {
        ImmutableList.Builder ruleListBuilder = ImmutableList.builder();
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(CROWD_SERVER_URL).check(DirectoryRuleBuilder.valueOf("crowd.server.url"), RuleBuilder.isNull().or(RuleBuilder.not(RuleBuilder.isValidURI()))).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directorycrowd.url.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(APPLICATION_NAME).check(DirectoryRuleBuilder.valueOf("application.name"), StringUtils::isBlank).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directorycrowd.applicationname.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(APPLICATION_PASSWORD).check(DirectoryRuleBuilder.valueOf("application.password"), StringUtils::isBlank).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directorycrowd.applicationpassword.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(CROWD_HTTP_PROXY_PORT).check(DirectoryRuleBuilder.valueOf("crowd.server.http.proxy.port"), RuleBuilder.notNull().and(RuleBuilder.not(RuleBuilder.inLongRange(0L, MAX_PORT_NUMBER)))).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directorycrowd.proxy.port.invalid")).build());
        return ruleListBuilder.build();
    }
}

