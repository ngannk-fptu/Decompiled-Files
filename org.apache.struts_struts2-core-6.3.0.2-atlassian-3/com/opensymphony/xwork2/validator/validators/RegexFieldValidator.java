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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegexFieldValidator
extends FieldValidatorSupport {
    private static final Logger LOG = LogManager.getLogger(RegexFieldValidator.class);
    private String regex;
    private String regexExpression;
    private Boolean caseSensitive = true;
    private String caseSensitiveExpression = "";
    private Boolean trim = true;
    private String trimExpression = "";

    @Override
    public void validate(Object object) throws ValidationException {
        String fieldName = this.getFieldName();
        Object value = this.getFieldValue(fieldName, object);
        String regexToUse = this.getRegex();
        LOG.debug("Defined regexp as [{}]", (Object)regexToUse);
        if (value == null || regexToUse == null) {
            LOG.debug("Either value is empty (please use a required validator) or regex is empty");
            return;
        }
        if (value.getClass().isArray()) {
            Object[] values;
            for (Object objValue : values = (Object[])value) {
                this.validateFieldValue(object, Objects.toString(objValue, ""), regexToUse);
            }
        } else if (Collection.class.isAssignableFrom(value.getClass())) {
            Collection values = (Collection)value;
            for (Object objValue : values) {
                this.validateFieldValue(object, Objects.toString(objValue, ""), regexToUse);
            }
        } else {
            this.validateFieldValue(object, Objects.toString(value, ""), regexToUse);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void validateFieldValue(Object object, String value, String regexToUse) {
        String str = value.trim();
        if (str.length() == 0) {
            LOG.debug("Value is empty, please use a required validator");
            return;
        }
        Pattern pattern = this.isCaseSensitive() ? Pattern.compile(regexToUse) : Pattern.compile(regexToUse, 2);
        String compare = value;
        if (this.isTrimed()) {
            compare = compare.trim();
        }
        try {
            this.setCurrentValue(compare);
            Matcher matcher = pattern.matcher(compare);
            if (!matcher.matches()) {
                this.addFieldError(this.fieldName, object);
            }
        }
        finally {
            this.setCurrentValue(null);
        }
    }

    public String getRegex() {
        if (StringUtils.isNotEmpty((CharSequence)this.regex)) {
            return this.regex;
        }
        if (StringUtils.isNotEmpty((CharSequence)this.regexExpression)) {
            return (String)this.parse(this.regexExpression, String.class);
        }
        return null;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public void setRegexExpression(String regexExpression) {
        this.regexExpression = regexExpression;
    }

    public boolean isCaseSensitive() {
        if (StringUtils.isNotEmpty((CharSequence)this.caseSensitiveExpression)) {
            return (Boolean)this.parse(this.caseSensitiveExpression, Boolean.class);
        }
        return this.caseSensitive;
    }

    public void setCaseSensitive(Boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public void setCaseSensitiveExpression(String caseSensitiveExpression) {
        this.caseSensitiveExpression = caseSensitiveExpression;
    }

    public boolean isTrimed() {
        if (StringUtils.isNotEmpty((CharSequence)this.trimExpression)) {
            return (Boolean)this.parse(this.trimExpression, Boolean.class);
        }
        return this.trim;
    }

    public void setTrim(Boolean trim) {
        this.trim = trim;
    }

    public void setTrimExpression(String trimExpression) {
        this.trimExpression = trimExpression;
    }
}

