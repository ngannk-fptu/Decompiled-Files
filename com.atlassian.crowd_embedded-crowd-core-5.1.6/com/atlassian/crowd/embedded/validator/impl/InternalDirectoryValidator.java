/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.util.I18nHelper
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.primitives.Longs
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
import com.google.common.primitives.Longs;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class InternalDirectoryValidator
extends DirectoryValidator {
    public static final String PASSWORD_REGEX = "passwordRegex";
    public static final String PASSWORD_MAX_CHANGE_TIME = "passwordMaxChangeTime";
    public static final String PASSWORD_MAX_AUTHENTICATE_ATTEMPTS = "passwordMaxAttempts";
    public static final String PASSWORD_HISTORY_COUNT = "passwordHistoryCount";
    public static final String USER_ENCRYPTION_METHOD = "userEncryptionMethod";
    public static final String REMIND_PERIODS = "passwordExpirationNotificationPeriods";
    private static final int MINIMUM_PASSWORD_MAX_CHANGE_TIME_TO_SET_REMINDERS = 3;

    public InternalDirectoryValidator(I18nHelper i18nHelper) {
        super(i18nHelper);
    }

    @Override
    protected List<ValidationRule<Directory>> initializeValidators(I18nHelper i18nHelper) {
        ImmutableList.Builder ruleListBuilder = ImmutableList.builder();
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(PASSWORD_REGEX).check(DirectoryRuleBuilder.valueOf("password_regex"), RuleBuilder.not(RuleBuilder.isValidRegex())).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryinternal.passwordregex.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(PASSWORD_MAX_AUTHENTICATE_ATTEMPTS).check(DirectoryRuleBuilder.valueOf("password_max_attempts"), RuleBuilder.not(RuleBuilder.greaterThanOrEquals(0L))).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryinternal.passwordmaxattempts.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(PASSWORD_MAX_CHANGE_TIME).check(DirectoryRuleBuilder.valueOf("password_max_change_time"), RuleBuilder.not(RuleBuilder.greaterThanOrEquals(0L))).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryinternal.passwordmaxchangetime.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(PASSWORD_HISTORY_COUNT).check(DirectoryRuleBuilder.valueOf("password_history_count"), RuleBuilder.not(RuleBuilder.greaterThanOrEquals(0L))).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryinternal.passwordhistorycount.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(USER_ENCRYPTION_METHOD).check(DirectoryRuleBuilder.valueOf("user_encryption_method"), StringUtils::isBlank).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryinternal.userencryptionmethod.invalid")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(REMIND_PERIODS).check(dir -> new PasswordExpirationAttributes(dir.getValue("password_expiration_notification_periods"), dir.getValue("password_max_change_time")), RuleBuilder.not(this::validRemindPeriodsFormat)).ifMatchesThenSet(RuleBuilder.message(i18nHelper, "directoryinternal.remindperiods.invalid.format")).build());
        ruleListBuilder.add(DirectoryRuleBuilder.ruleFor(REMIND_PERIODS).check(dir -> new PasswordExpirationAttributes(dir.getValue("password_expiration_notification_periods"), dir.getValue("password_max_change_time")), RuleBuilder.not(this::validRemindPeriodsValues)).ifMatchesThenSet(dir -> {
            int passwordMaxChangeTime = Integer.parseInt(dir.getValue("password_max_change_time"));
            if (passwordMaxChangeTime < 3) {
                return i18nHelper.getText("directoryinternal.passwordmaxchangetime.too.low");
            }
            return i18nHelper.getText("directoryinternal.remindperiods.invalid.values", (Object)ImmutableList.of((Object)passwordMaxChangeTime, (Object)(passwordMaxChangeTime - 1)));
        }).build());
        return ruleListBuilder.build();
    }

    private boolean validRemindPeriodsValues(PasswordExpirationAttributes attributes) {
        if (attributes.getRemindPeriods().size() == 0 || attributes.getRemindPeriods().contains(null)) {
            return true;
        }
        TreeSet remindPeriods = new TreeSet(Comparator.reverseOrder());
        remindPeriods.addAll(attributes.getRemindPeriods());
        Long passwordMaxChangeTime = Longs.tryParse((String)attributes.getPasswordMaxChangeTime());
        return remindPeriods.size() > 0 && (Long)remindPeriods.last() > 0L && (Long)remindPeriods.first() < passwordMaxChangeTime && passwordMaxChangeTime >= 3L;
    }

    private boolean validRemindPeriodsFormat(PasswordExpirationAttributes attributes) {
        HashSet<Long> uniqueRemindPeriods = new HashSet<Long>(attributes.getRemindPeriods());
        return uniqueRemindPeriods.size() == attributes.getRemindPeriods().size() && !attributes.getRemindPeriods().contains(null);
    }

    private static class PasswordExpirationAttributes {
        private final String passwordMaxChangeTime;
        private final List<Long> remindPeriodsAsArray;

        public PasswordExpirationAttributes(String remindPeriods, String passwordMaxChangeTime) {
            this.remindPeriodsAsArray = this.parseRemindPeriodsAsArray(remindPeriods);
            this.passwordMaxChangeTime = passwordMaxChangeTime;
        }

        public String getPasswordMaxChangeTime() {
            return this.passwordMaxChangeTime;
        }

        public List<Long> getRemindPeriods() {
            return this.remindPeriodsAsArray;
        }

        protected List<Long> parseRemindPeriodsAsArray(String remindPeriods) {
            return remindPeriods.equals("") ? Collections.emptyList() : Arrays.stream(remindPeriods.split(",")).map(String::trim).map(Longs::tryParse).collect(Collectors.toList());
        }
    }
}

