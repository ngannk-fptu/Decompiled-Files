/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.FieldValidatorSupport;
import java.util.Collection;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StringLengthFieldValidator
extends FieldValidatorSupport {
    private static final Logger LOG = LogManager.getLogger(StringLengthFieldValidator.class);
    private boolean trim = true;
    private int maxLength = -1;
    private int minLength = -1;
    private String maxLengthExpression;
    private String minLengthExpression;
    private String trimExpression;

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setMaxLengthExpression(String maxLengthExpression) {
        this.maxLengthExpression = maxLengthExpression;
    }

    public int getMaxLength() {
        if (StringUtils.isNotEmpty((CharSequence)this.maxLengthExpression)) {
            return (Integer)this.parse(this.maxLengthExpression, Integer.class);
        }
        return this.maxLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public void setMinLengthExpression(String minLengthExpression) {
        this.minLengthExpression = minLengthExpression;
    }

    public int getMinLength() {
        if (StringUtils.isNotEmpty((CharSequence)this.minLengthExpression)) {
            return (Integer)this.parse(this.minLengthExpression, Integer.class);
        }
        return this.minLength;
    }

    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    public void setTrimExpression(String trimExpression) {
        this.trimExpression = trimExpression;
    }

    public boolean isTrim() {
        if (StringUtils.isNotEmpty((CharSequence)this.trimExpression)) {
            return (Boolean)this.parse(this.trimExpression, Boolean.class);
        }
        return this.trim;
    }

    @Override
    public void validate(Object object) throws ValidationException {
        Object fieldValue = this.getFieldValue(this.fieldName, object);
        if (fieldValue == null) {
            LOG.debug("Value for field {} is null, use a required validator", (Object)this.getFieldName());
        } else if (fieldValue.getClass().isArray()) {
            Object[] values;
            for (Object value : values = (Object[])fieldValue) {
                this.validateValue(object, value);
            }
        } else if (Collection.class.isAssignableFrom(fieldValue.getClass())) {
            Collection values = (Collection)fieldValue;
            for (Object value : values) {
                this.validateValue(object, value);
            }
        } else {
            this.validateValue(object, fieldValue);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void validateValue(Object object, Object value) {
        String stringValue = Objects.toString(value, "");
        if (StringUtils.isEmpty((CharSequence)stringValue)) {
            LOG.debug("Value is empty, use a required validator");
            return;
        }
        if (this.isTrim() && StringUtils.isEmpty((CharSequence)(stringValue = stringValue.trim()))) {
            LOG.debug("Value is empty, use a required validator");
            return;
        }
        int minLengthToUse = this.getMinLength();
        int maxLengthToUse = this.getMaxLength();
        try {
            this.setCurrentValue(stringValue);
            if (minLengthToUse > -1 && stringValue.length() < minLengthToUse) {
                this.addFieldError(this.fieldName, object);
            } else if (maxLengthToUse > -1 && stringValue.length() > maxLengthToUse) {
                this.addFieldError(this.fieldName, object);
            }
        }
        finally {
            this.setCurrentValue(null);
        }
    }
}

