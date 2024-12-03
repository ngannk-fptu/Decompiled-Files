/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.GenericLDAP
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.util.I18nHelper
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.crowd.embedded.validator.impl;

import com.atlassian.crowd.directory.GenericLDAP;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.validator.DirectoryValidator;
import com.atlassian.crowd.embedded.validator.ValidationRule;
import com.atlassian.crowd.embedded.validator.rule.DirectoryRuleBuilder;
import com.atlassian.crowd.embedded.validator.rule.RuleBuilder;
import com.atlassian.crowd.util.I18nHelper;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class LDAPConnectionValidator
extends DirectoryValidator {
    public static final String BASE_DN_KEY = "baseDN";
    public static final String LDAP_URL_KEY = "URL";
    private static final String VALID_DN_PATTERN = ".+=.+";

    public LDAPConnectionValidator(I18nHelper i18nHelper) {
        super(i18nHelper);
    }

    @Override
    protected List<ValidationRule<Directory>> initializeValidators(I18nHelper i18nHelper) {
        ImmutableList.Builder ruleListBuilder = ImmutableList.builder();
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(LDAP_URL_KEY).check(DirectoryRuleBuilder.valueOf("ldap.url"), StringUtils::isBlank).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryconnector.url.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(BASE_DN_KEY).check(Directory::getImplementationClass, RuleBuilder.not(RuleBuilder.eq(GenericLDAP.class.getCanonicalName()))).check(DirectoryRuleBuilder.valueOf("ldap.basedn"), StringUtils::isBlank).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryconnector.basedn.invalid.blank")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(BASE_DN_KEY).check(Directory::getImplementationClass, RuleBuilder.not(RuleBuilder.eq(GenericLDAP.class.getCanonicalName()))).check(DirectoryRuleBuilder.valueOf("ldap.basedn"), RuleBuilder.matchesAll(StringUtils::isNotBlank, RuleBuilder.not(RuleBuilder.regex(VALID_DN_PATTERN)))).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryconnector.basedn.invalid")).build());
        return ruleListBuilder.build();
    }
}

