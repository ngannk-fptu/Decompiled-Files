/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.ImmutableMultimap$Builder
 *  com.google.common.collect.ImmutableSetMultimap
 *  com.google.common.collect.ImmutableSetMultimap$Builder
 *  com.google.common.collect.Multimap
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.authentication.impl.config;

import com.atlassian.plugins.authentication.api.config.AbstractIdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.JustInTimeConfig;
import com.atlassian.plugins.authentication.api.config.SsoType;
import com.atlassian.plugins.authentication.api.config.ValidationError;
import com.atlassian.plugins.authentication.impl.config.IdpConfigValidator;
import com.atlassian.plugins.authentication.impl.config.ValidationContext;
import com.atlassian.plugins.authentication.impl.util.HttpsValidator;
import com.atlassian.plugins.authentication.impl.util.ValidationUtils;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.mapping.MappingExpression;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.mapping.MappingExpressionException;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import java.net.URL;
import java.util.Collections;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractIdpConfigValidator<T extends AbstractIdpConfig>
implements IdpConfigValidator {
    private static final String EXPRESSION_ERROR_SPECIFIC_MESSAGE_KEY = "message";
    private static final String EXPRESSION_ERROR_INDEX_KEY = "index";
    private static final String EXPRESSION_ERROR_ORIGINAL_EXPRESSION_KEY = "expression";
    public static final int BUTTON_TEXT_LIMIT = 40;
    protected static final Iterable<ValidationError> ERROR_REQUIRED = Collections.singleton(ValidationError.required());
    protected static final Iterable<ValidationError> ERROR_INCORRECT = Collections.singleton(ValidationError.incorrect());
    protected static final Iterable<ValidationError> ERROR_INSECURE = Collections.singleton(ValidationError.insecure());
    protected static final Iterable<ValidationError> ERROR_TOO_LONG = Collections.singleton(ValidationError.tooLong());
    protected static final Iterable<ValidationError> NO_ERRORS = Collections.emptyList();
    protected final HttpsValidator httpsValidator;

    protected AbstractIdpConfigValidator(HttpsValidator httpsValidator) {
        this.httpsValidator = httpsValidator;
    }

    protected abstract SsoType getSsoType();

    protected abstract Class<T> getSsoClass();

    protected abstract void validate(@Nonnull ImmutableMultimap.Builder<String, ValidationError> var1, @Nonnull T var2);

    @Override
    @Nonnull
    public final Multimap<String, ValidationError> validate(@Nonnull IdpConfig ssoConfig) {
        Preconditions.checkArgument((ssoConfig.getSsoType() == this.getSsoType() ? 1 : 0) != 0, (Object)("Unsupported SSO type: " + (Object)((Object)ssoConfig.getSsoType())));
        Preconditions.checkArgument((boolean)this.getSsoClass().isInstance(ssoConfig), (Object)("Unsupported config type: " + ssoConfig.getClass()));
        ImmutableSetMultimap.Builder errors = ImmutableSetMultimap.builder();
        errors.putAll((Object)"sso-type", this.validateRequiredField((Object)ssoConfig.getSsoType()));
        errors.putAll((Object)"name", this.validateRequiredField(ssoConfig.getName()));
        errors.putAll((Object)"button-text", this.validateRequiredField(ssoConfig.getButtonText()));
        errors.putAll((Object)"button-text", this.validateLength(ssoConfig.getButtonText(), 40));
        this.validate((ImmutableMultimap.Builder<String, ValidationError>)errors, (AbstractIdpConfig)this.getSsoClass().cast(ssoConfig));
        return errors.build();
    }

    @Override
    @Nonnull
    public final Multimap<String, ValidationError> validate(@Nonnull IdpConfig config, ValidationContext context) {
        if (context == ValidationContext.FULL_VALIDATION) {
            return this.validate(config);
        }
        return this.validateInContext((AbstractIdpConfig)this.getSsoClass().cast(config), context);
    }

    protected abstract Multimap<String, ValidationError> validateInContext(T var1, ValidationContext var2);

    protected Iterable<ValidationError> validateRequiredField(Object value) {
        boolean empty = value instanceof String ? StringUtils.isEmpty((CharSequence)((String)value)) : value == null;
        return empty ? ERROR_REQUIRED : NO_ERRORS;
    }

    protected Iterable<ValidationError> validateUrl(String ssoUrl) {
        if (!Strings.isNullOrEmpty((String)ssoUrl)) {
            if (!this.isValidUrl(ssoUrl)) {
                return ERROR_INCORRECT;
            }
            if (!this.isSecureUrl(ssoUrl)) {
                return ERROR_INSECURE;
            }
        }
        return NO_ERRORS;
    }

    private boolean isSecureUrl(String ssoUrl) {
        if (this.httpsValidator.isHttpsRequired() && !Strings.isNullOrEmpty((String)ssoUrl)) {
            URL url = ValidationUtils.convertToUrl(ssoUrl);
            return url == null || "https".equalsIgnoreCase(url.getProtocol());
        }
        return true;
    }

    protected Multimap<String, ValidationError> validateJitFields(JustInTimeConfig justInTimeConfig) {
        ImmutableMultimap.Builder errors = ImmutableMultimap.builder();
        if (justInTimeConfig != null && justInTimeConfig.isEnabled().orElse(false).booleanValue()) {
            errors.putAll((Object)"mapping-display-name", this.validateMappingExpression(justInTimeConfig.getDisplayNameMappingExpression().orElse(null)));
            errors.putAll((Object)"mapping-email", this.validateMappingExpression(justInTimeConfig.getEmailMappingExpression().orElse(null)));
            errors.putAll((Object)"mapping-groups", this.validateNotMappingExpression(justInTimeConfig.getGroupsMappingSource().orElse(null)));
        }
        return errors.build();
    }

    protected Iterable<ValidationError> validateMappingExpression(String expression) {
        if (!Strings.isNullOrEmpty((String)expression)) {
            try {
                MappingExpression.validate(expression);
                return NO_ERRORS;
            }
            catch (MappingExpressionException exception) {
                ValidationError error = ValidationError.incorrect();
                error.getMetadata().put(EXPRESSION_ERROR_SPECIFIC_MESSAGE_KEY, exception.getFriendlyMessage());
                error.getMetadata().put(EXPRESSION_ERROR_ORIGINAL_EXPRESSION_KEY, expression);
                error.getMetadata().put(EXPRESSION_ERROR_INDEX_KEY, exception.getIndexOfException());
                return Collections.singleton(error);
            }
        }
        return ERROR_REQUIRED;
    }

    protected Iterable<ValidationError> validateNotMappingExpression(String value) {
        if (Strings.isNullOrEmpty((String)value)) {
            return ERROR_REQUIRED;
        }
        if (MappingExpression.containsVariableOpenerOrCloser(value)) {
            return Collections.singleton(ValidationError.notSupported());
        }
        return NO_ERRORS;
    }

    protected Iterable<ValidationError> validateLength(String value, int limit) {
        return Strings.nullToEmpty((String)value).length() > limit ? ERROR_TOO_LONG : NO_ERRORS;
    }

    private boolean isValidUrl(String urlString) {
        if (!Strings.isNullOrEmpty((String)urlString)) {
            try {
                ValidationUtils.convertToUrl(urlString);
                return true;
            }
            catch (IllegalArgumentException e) {
                return false;
            }
        }
        return true;
    }
}

