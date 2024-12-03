/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.validation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;

public abstract class ValidationUtils {
    private static final Log logger = LogFactory.getLog(ValidationUtils.class);

    public static void invokeValidator(Validator validator, Object obj, Errors errors) {
        ValidationUtils.invokeValidator(validator, obj, errors, null);
    }

    public static void invokeValidator(Validator validator, @Nullable Object obj, Errors errors, Object ... validationHints) {
        Assert.notNull((Object)validator, "Validator must not be null");
        Assert.notNull((Object)errors, "Errors object must not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Invoking validator [" + validator + "]");
        }
        if (obj != null && !validator.supports(obj.getClass())) {
            throw new IllegalArgumentException("Validator [" + validator.getClass() + "] does not support [" + obj.getClass() + "]");
        }
        if (!ObjectUtils.isEmpty(validationHints) && validator instanceof SmartValidator) {
            ((SmartValidator)validator).validate(obj, errors, validationHints);
        } else {
            validator.validate(obj, errors);
        }
        if (logger.isDebugEnabled()) {
            if (errors.hasErrors()) {
                logger.debug("Validator found " + errors.getErrorCount() + " errors");
            } else {
                logger.debug("Validator found no errors");
            }
        }
    }

    public static void rejectIfEmpty(Errors errors, String field, String errorCode) {
        ValidationUtils.rejectIfEmpty(errors, field, errorCode, null, null);
    }

    public static void rejectIfEmpty(Errors errors, String field, String errorCode, String defaultMessage) {
        ValidationUtils.rejectIfEmpty(errors, field, errorCode, null, defaultMessage);
    }

    public static void rejectIfEmpty(Errors errors, String field, String errorCode, Object[] errorArgs) {
        ValidationUtils.rejectIfEmpty(errors, field, errorCode, errorArgs, null);
    }

    public static void rejectIfEmpty(Errors errors, String field, String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage) {
        Assert.notNull((Object)errors, "Errors object must not be null");
        Object value = errors.getFieldValue(field);
        if (value == null || !StringUtils.hasLength(value.toString())) {
            errors.rejectValue(field, errorCode, errorArgs, defaultMessage);
        }
    }

    public static void rejectIfEmptyOrWhitespace(Errors errors, String field, String errorCode) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, field, errorCode, null, null);
    }

    public static void rejectIfEmptyOrWhitespace(Errors errors, String field, String errorCode, String defaultMessage) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, field, errorCode, null, defaultMessage);
    }

    public static void rejectIfEmptyOrWhitespace(Errors errors, String field, String errorCode, @Nullable Object[] errorArgs) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, field, errorCode, errorArgs, null);
    }

    public static void rejectIfEmptyOrWhitespace(Errors errors, String field, String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage) {
        Assert.notNull((Object)errors, "Errors object must not be null");
        Object value = errors.getFieldValue(field);
        if (value == null || !StringUtils.hasText(value.toString())) {
            errors.rejectValue(field, errorCode, errorArgs, defaultMessage);
        }
    }
}

