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

public class LDAPGroupConfigValidator
extends DirectoryValidator {
    public static final String GROUP_DESCRIPTION_ATTRIBUTE = "groupDescriptionAttr";
    public static final String GROUP_MEMBER_ATTRIBUTE = "groupMemberAttr";
    public static final String GROUP_NAME_ATTRIBUTE = "groupNameAttr";
    public static final String GROUP_OBJECT_CLASS = "groupObjectClass";
    public static final String GROUP_OBJECT_FILTER = "groupObjectFilter";

    public LDAPGroupConfigValidator(I18nHelper i18nHelper) {
        super(i18nHelper);
    }

    @Override
    protected List<ValidationRule<Directory>> initializeValidators(I18nHelper i18nHelper) {
        ImmutableList.Builder ruleListBuilder = ImmutableList.builder();
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(GROUP_OBJECT_CLASS).check(DirectoryRuleBuilder.valueOf("ldap.group.objectclass"), StringUtils::isBlank).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryconnector.groupobjectclass.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(GROUP_OBJECT_FILTER).check(DirectoryRuleBuilder.valueOf("ldap.group.filter"), StringUtils::isBlank).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryconnector.groupobjectfilter.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(GROUP_NAME_ATTRIBUTE).check(DirectoryRuleBuilder.valueOf("ldap.group.name"), StringUtils::isBlank).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryconnector.groupname.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(GROUP_DESCRIPTION_ATTRIBUTE).check(DirectoryRuleBuilder.valueOf("ldap.group.description"), StringUtils::isBlank).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryconnector.groupdescription.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(GROUP_MEMBER_ATTRIBUTE).check(DirectoryRuleBuilder.valueOf("ldap.group.usernames"), StringUtils::isBlank).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryconnector.groupmember.invalid")).build());
        return ruleListBuilder.build();
    }
}

