/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.cron.CronExpressionValidator
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableSetMultimap
 *  com.google.common.collect.ImmutableSetMultimap$Builder
 *  com.google.common.collect.Multimap
 *  javax.annotation.Nonnull
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.plugins.authentication.impl.config;

import com.atlassian.plugins.authentication.api.config.SsoConfig;
import com.atlassian.plugins.authentication.api.config.ValidationError;
import com.atlassian.plugins.authentication.impl.config.AbstractIdpConfigValidator;
import com.atlassian.plugins.authentication.impl.config.SsoConfigValidator;
import com.atlassian.scheduler.cron.CronExpressionValidator;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class SsoConfigValidatorImpl
implements SsoConfigValidator {
    private final CronExpressionValidator cronExpressionValidator;

    @Inject
    public SsoConfigValidatorImpl(CronExpressionValidator cronExpressionValidator) {
        this.cronExpressionValidator = cronExpressionValidator;
    }

    @Override
    @Nonnull
    public Multimap<String, ValidationError> validate(@Nonnull SsoConfig config) {
        ImmutableSetMultimap.Builder errors = ImmutableSetMultimap.builder();
        errors.putAll((Object)"discovery-refresh-cron", this.validateCronExpression(config.getDiscoveryRefreshCron()));
        return errors.build();
    }

    private Iterable<ValidationError> validateCronExpression(String cronExpression) {
        if (!Strings.isNullOrEmpty((String)cronExpression) && !this.cronExpressionValidator.isValid(cronExpression)) {
            return AbstractIdpConfigValidator.ERROR_INCORRECT;
        }
        return AbstractIdpConfigValidator.NO_ERRORS;
    }
}

