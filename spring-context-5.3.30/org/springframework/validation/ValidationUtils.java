/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
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

    public static void invokeValidator(Validator validator, Object target, Errors errors) {
        ValidationUtils.invokeValidator(validator, target, errors, null);
    }

    public static void invokeValidator(Validator validator, Object target, Errors errors, Object ... validationHints) {
        Assert.notNull((Object)validator, (String)"Validator must not be null");
        Assert.notNull((Object)target, (String)"Target object must not be null");
        Assert.notNull((Object)errors, (String)"Errors object must not be null");
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("Invoking validator [" + validator + "]"));
        }
        if (!validator.supports(target.getClass())) {
            throw new IllegalArgumentException("Validator [" + validator.getClass() + "] does not support [" + target.getClass() + "]");
        }
        if (!ObjectUtils.isEmpty((Object[])validationHints) && validator instanceof SmartValidator) {
            ((SmartValidator)validator).validate(target, errors, validationHints);
        } else {
            validator.validate(target, errors);
        }
        if (logger.isDebugEnabled()) {
            if (errors.hasErrors()) {
                logger.debug((Object)("Validator found " + errors.getErrorCount() + " errors"));
            } else {
                logger.debug((Object)"Validator found no errors");
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
        Assert.notNull((Object)errors, (String)"Errors object must not be null");
        Object value = errors.getFieldValue(field);
        if (value == null || !StringUtils.hasLength((String)value.toString())) {
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
        Assert.notNull((Object)errors, (String)"Errors object must not be null");
        Object value = errors.getFieldValue(field);
        if (value == null || !StringUtils.hasText((String)value.toString())) {
            errors.rejectValue(field, errorCode, errorArgs, defaultMessage);
        }
    }
}

