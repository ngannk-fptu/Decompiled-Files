/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.Message
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.exception;

import com.atlassian.applinks.internal.common.exception.SimpleDetailedErrors;
import com.atlassian.applinks.internal.common.exception.ValidationException;
import com.atlassian.applinks.internal.common.i18n.I18nKey;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.Message;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.io.Serializable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ValidationExceptionBuilder {
    private static final String DEFAULT_MESSAGE = "applinks.service.error.validation";
    private final I18nResolver i18nResolver;
    private Object origin;
    private String originName;
    private Throwable cause;
    private final SimpleDetailedErrors.Builder errorsBuilder = new SimpleDetailedErrors.Builder();

    public ValidationExceptionBuilder(I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    @Nonnull
    public ValidationExceptionBuilder origin(@Nonnull Object origin) {
        this.origin = origin;
        return this;
    }

    @Nonnull
    public ValidationExceptionBuilder originName(@Nullable String originName) {
        this.originName = originName;
        return this;
    }

    @Nonnull
    public ValidationExceptionBuilder cause(@Nullable Throwable cause) {
        this.cause = cause;
        return this;
    }

    @Nonnull
    public ValidationExceptionBuilder error(@Nullable String context, @Nonnull I18nKey summaryKey, @Nullable String details) {
        this.errorsBuilder.error(context, this.i18nResolver.getText((Message)summaryKey), details);
        return this;
    }

    @Nonnull
    public ValidationExceptionBuilder error(@Nullable String context, @Nonnull String summaryKey, Serializable ... args) {
        return this.error(context, I18nKey.newI18nKey(summaryKey, args), null);
    }

    @Nonnull
    public ValidationExceptionBuilder error(@Nonnull String summaryKey, Serializable ... args) {
        return this.error(null, summaryKey, args);
    }

    public boolean hasErrors() {
        return this.errorsBuilder.hasErrors();
    }

    @Nonnull
    public ValidationException build() {
        Preconditions.checkState((this.origin != null ? 1 : 0) != 0, (Object)"Origin was not set");
        Preconditions.checkState((boolean)this.hasErrors(), (Object)"There were no errors");
        SimpleDetailedErrors errors = this.errorsBuilder.build();
        String originName = this.originName == null ? this.origin.getClass().getSimpleName() : this.originName;
        String message = this.i18nResolver.getText(DEFAULT_MESSAGE, new Serializable[]{originName, Integer.valueOf(Iterables.size(errors.getErrors()))});
        return new ValidationException(this.origin, errors.getErrors(), message, this.cause);
    }
}

