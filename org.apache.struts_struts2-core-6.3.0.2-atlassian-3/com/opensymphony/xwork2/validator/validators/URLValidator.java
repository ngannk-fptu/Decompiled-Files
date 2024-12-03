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
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class URLValidator
extends FieldValidatorSupport {
    private static final Logger LOG = LogManager.getLogger(URLValidator.class);
    public static final String DEFAULT_URL_REGEX = "^(?:https?|ftp):\\/\\/(?:(?:[a-z0-9$_.+!*'(),;?&=\\-]|%[0-9a-f]{2})+(?::(?:[a-z0-9$_.+!*'(),;?&=\\-]|%[0-9a-f]{2})+)?@)?#?(?:(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)*[a-z][a-z0-9-]*[a-z0-9]|(?:(?:[1-9]?\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}(?:[1-9]?\\d|1\\d{2}|2[0-4]\\d|25[0-5]))(?::\\d+)?)(?:(?:\\/(?:[a-z0-9$_.+!*'(),;:@&=\\-]|%[0-9a-f]{2})*)*(?:\\?(?:[a-z0-9$_.+!*'(),;:@&=\\-\\/:]|%[0-9a-f]{2})*)?)?(?:#(?:[a-z0-9$_.+!*'(),;:@&=\\-]|%[0-9a-f]{2})*)?$";
    private String urlRegexExpression;
    private Pattern urlPattern = Pattern.compile("^(?:https?|ftp):\\/\\/(?:(?:[a-z0-9$_.+!*'(),;?&=\\-]|%[0-9a-f]{2})+(?::(?:[a-z0-9$_.+!*'(),;?&=\\-]|%[0-9a-f]{2})+)?@)?#?(?:(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)*[a-z][a-z0-9-]*[a-z0-9]|(?:(?:[1-9]?\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}(?:[1-9]?\\d|1\\d{2}|2[0-4]\\d|25[0-5]))(?::\\d+)?)(?:(?:\\/(?:[a-z0-9$_.+!*'(),;:@&=\\-]|%[0-9a-f]{2})*)*(?:\\?(?:[a-z0-9$_.+!*'(),;:@&=\\-\\/:]|%[0-9a-f]{2})*)?)?(?:#(?:[a-z0-9$_.+!*'(),;:@&=\\-]|%[0-9a-f]{2})*)?$", 2);

    @Override
    public void validate(Object object) throws ValidationException {
        Object value = this.getFieldValue(this.fieldName, object);
        String stringValue = Objects.toString(value, "").trim();
        if (stringValue.length() == 0) {
            LOG.debug("Value for field {} is empty, won't ba validated, please use a required validator", (Object)this.fieldName);
            return;
        }
        if (value.getClass().isArray()) {
            Object[] values;
            for (Object objValue : values = (Object[])value) {
                LOG.debug("Validating element of array: {}", objValue);
                this.validateValue(object, objValue);
            }
        } else if (Collection.class.isAssignableFrom(value.getClass())) {
            Collection values = (Collection)value;
            for (Object objValue : values) {
                LOG.debug("Validating element of collection: {}", objValue);
                this.validateValue(object, objValue);
            }
        } else {
            LOG.debug("Validating field: {}", value);
            this.validateValue(object, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void validateValue(Object object, Object value) {
        String stringValue = Objects.toString(value, "").trim();
        if (stringValue.length() == 0) {
            LOG.debug("Value for field {} is empty, won't ba validated, please use a required validator", (Object)this.fieldName);
            return;
        }
        try {
            this.setCurrentValue(value);
            if (!value.getClass().equals(String.class) || !this.getUrlPattern().matcher(stringValue).matches()) {
                this.addFieldError(this.fieldName, object);
            }
        }
        finally {
            this.setCurrentValue(null);
        }
    }

    protected Pattern getUrlPattern() {
        if (StringUtils.isNotEmpty((CharSequence)this.urlRegexExpression)) {
            String regex = (String)this.parse(this.urlRegexExpression, String.class);
            if (regex == null) {
                LOG.warn("Provided URL Regex expression [{}] was evaluated to null! Falling back to default!", (Object)this.urlRegexExpression);
                this.urlPattern = Pattern.compile(DEFAULT_URL_REGEX, 2);
            } else {
                this.urlPattern = Pattern.compile(regex, 2);
            }
        }
        return this.urlPattern;
    }

    public String getUrlRegex() {
        return this.getUrlPattern().pattern();
    }

    public void setUrlRegex(String urlRegex) {
        this.urlPattern = Pattern.compile(urlRegex, 2);
    }

    public void setUrlRegexExpression(String urlRegexExpression) {
        this.urlRegexExpression = urlRegexExpression;
    }
}

