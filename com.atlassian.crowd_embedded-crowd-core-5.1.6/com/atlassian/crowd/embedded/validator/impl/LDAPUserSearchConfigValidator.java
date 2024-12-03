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

public class LDAPUserSearchConfigValidator
extends DirectoryValidator {
    public LDAPUserSearchConfigValidator(I18nHelper i18nHelper) {
        super(i18nHelper);
    }

    @Override
    protected List<ValidationRule<Directory>> initializeValidators(I18nHelper i18nHelper) {
        ImmutableList.Builder ruleListBuilder = ImmutableList.builder();
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor("userObjectFilter").check(DirectoryRuleBuilder.valueOf("ldap.user.filter"), StringUtils::isBlank).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryconnector.userobjectfilter.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor("userNameAttr").check(DirectoryRuleBuilder.valueOf("ldap.user.username"), StringUtils::isBlank).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryconnector.usernameattribute.invalid")).build());
        return ruleListBuilder.build();
    }
}

