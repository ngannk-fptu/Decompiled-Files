/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.annotation.Nonnull
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.oauth2.common.rest.validator;

import com.atlassian.oauth2.common.rest.validator.ErrorCollection;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.Serializable;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class RestValidator {
    public static final int MAX_CHARACTER_LENGTH = 255;
    public static final String FIELD_REQUIRED = "oauth2.rest.error.settings.field.required";
    public static final String FIELD_TOO_LONG = "oauth2.rest.error.settings.field.too.long";
    private final I18nResolver i18nResolver;

    public RestValidator(I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    protected String checkNotEmpty(ErrorCollection.Builder errors, String fieldName, String fieldValue) {
        this.checkField(errors, fieldName, StringUtils.isNotBlank((CharSequence)fieldValue), () -> this.i18nResolver.getText(FIELD_REQUIRED, new Serializable[]{fieldName}));
        return fieldValue;
    }

    protected <T, C extends Collection<T>> C checkNotEmpty(ErrorCollection.Builder errors, String fieldName, C fieldValue) {
        this.checkField(errors, fieldName, CollectionUtils.isNotEmpty(fieldValue), () -> this.i18nResolver.getText(FIELD_REQUIRED, new Serializable[]{fieldName}));
        return fieldValue;
    }

    protected String checkNotTooLong(ErrorCollection.Builder errors, String fieldName, String fieldValue) {
        this.checkField(errors, fieldName, StringUtils.length((CharSequence)fieldValue) <= 255, () -> this.i18nResolver.getText(FIELD_TOO_LONG, new Serializable[]{fieldName, Integer.valueOf(255)}));
        return fieldValue;
    }

    protected void checkField(@Nonnull ErrorCollection.Builder errors, @Nonnull String fieldName, boolean condition, @Nonnull Supplier<String> message) {
        if (!condition) {
            errors.addFieldErrors(fieldName, message.get());
        }
    }

    protected void checkField(@Nonnull ErrorCollection.Builder errors, @Nonnull String fieldName, String result, @Nonnull Function<String, String> message) {
        if (StringUtils.isNotEmpty((CharSequence)result)) {
            errors.addFieldErrors(fieldName, message.apply(result));
        }
    }
}

