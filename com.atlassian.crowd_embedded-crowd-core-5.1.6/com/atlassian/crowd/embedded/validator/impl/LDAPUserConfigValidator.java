/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.DelegatedAuthenticationDirectory
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.util.I18nHelper
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.crowd.embedded.validator.impl;

import com.atlassian.crowd.directory.DelegatedAuthenticationDirectory;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.validator.DirectoryValidator;
import com.atlassian.crowd.embedded.validator.ValidationRule;
import com.atlassian.crowd.embedded.validator.rule.DirectoryRuleBuilder;
import com.atlassian.crowd.embedded.validator.rule.RuleBuilder;
import com.atlassian.crowd.util.I18nHelper;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class LDAPUserConfigValidator
extends DirectoryValidator {
    public static final String USER_FIRST_NAME_ATTRIBUTE = "userFirstnameAttr";
    public static final String USER_GROUP_MEMBER_ATTRIBUTE = "userGroupMemberAttr";
    public static final String USER_LAST_NAME_ATTRIBUTE = "userLastnameAttr";
    public static final String USER_DISPLAY_NAME_ATTRIBUTE = "userDisplayNameAttr";
    public static final String USER_MAIL_ATTRIBUTE = "userMailAttr";
    public static final String USER_NAME_ATTRIBUTE = "userNameAttr";
    public static final String USERNAME_RDN_ATTRIBUTE = "userNameRdnAttr";
    public static final String USER_OBJECT_FILTER = "userObjectFilter";
    public static final String USER_OBJECT_CLASS = "userObjectClass";
    public static final String USER_PASSWORD_ATTRIBUTE = "userPasswordAttr";

    public LDAPUserConfigValidator(I18nHelper i18nHelper) {
        super(i18nHelper);
    }

    @Override
    protected List<ValidationRule<Directory>> initializeValidators(I18nHelper i18nHelper) {
        ImmutableList.Builder ruleListBuilder = ImmutableList.builder();
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(USER_OBJECT_CLASS).check(DirectoryRuleBuilder.valueOf("ldap.user.objectclass"), StringUtils::isBlank).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryconnector.userobjectclass.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(USER_OBJECT_FILTER).check(DirectoryRuleBuilder.valueOf("ldap.user.filter"), StringUtils::isBlank).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryconnector.userobjectfilter.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(USER_NAME_ATTRIBUTE).check(DirectoryRuleBuilder.valueOf("ldap.user.username"), StringUtils::isBlank).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryconnector.usernameattribute.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(USERNAME_RDN_ATTRIBUTE).check(DirectoryRuleBuilder.valueOf("ldap.user.username.rdn"), StringUtils::isBlank).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryconnector.usernamerdnattribute.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(USER_FIRST_NAME_ATTRIBUTE).check(DirectoryRuleBuilder.valueOf("ldap.user.firstname"), StringUtils::isBlank).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryconnector.userfirstnameattribute.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(USER_LAST_NAME_ATTRIBUTE).check(DirectoryRuleBuilder.valueOf("ldap.user.lastname"), StringUtils::isBlank).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryconnector.userlastnameattribute.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(USER_DISPLAY_NAME_ATTRIBUTE).check(DirectoryRuleBuilder.valueOf("ldap.user.displayname"), StringUtils::isBlank).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryconnector.userdisplaynameattribute.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(USER_MAIL_ATTRIBUTE).check(DirectoryRuleBuilder.valueOf("ldap.user.email"), StringUtils::isBlank).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryconnector.usermailattribute.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(USER_GROUP_MEMBER_ATTRIBUTE).check(DirectoryRuleBuilder.valueOf("ldap.user.group"), StringUtils::isBlank).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryconnector.usermemberofattribute.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(USER_PASSWORD_ATTRIBUTE).check(Directory::getImplementationClass, RuleBuilder.not(RuleBuilder.eq(DelegatedAuthenticationDirectory.class.getName()))).check(DirectoryRuleBuilder.valueOf("ldap.user.password"), StringUtils::isBlank).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryconnector.userpassword.invalid")).build());
        return ruleListBuilder.build();
    }
}

