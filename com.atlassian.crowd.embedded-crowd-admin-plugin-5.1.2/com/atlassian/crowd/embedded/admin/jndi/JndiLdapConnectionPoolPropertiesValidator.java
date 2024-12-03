/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.ConnectionPoolProperties
 *  com.atlassian.crowd.embedded.impl.ConnectionPoolPropertyUtil
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.validation.Errors
 *  org.springframework.validation.Validator
 */
package com.atlassian.crowd.embedded.admin.jndi;

import com.atlassian.crowd.embedded.api.ConnectionPoolProperties;
import com.atlassian.crowd.embedded.impl.ConnectionPoolPropertyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class JndiLdapConnectionPoolPropertiesValidator
implements Validator {
    public boolean supports(Class clazz) {
        return ConnectionPoolProperties.class.isAssignableFrom(clazz);
    }

    public void validate(Object target, Errors errors) {
        ConnectionPoolProperties configuration = (ConnectionPoolProperties)target;
        if (!StringUtils.isNumeric((CharSequence)configuration.getInitialSize())) {
            errors.rejectValue("initialSize", "invalid");
        }
        if (!StringUtils.isNumeric((CharSequence)configuration.getPreferredSize())) {
            errors.rejectValue("preferredSize", "invalid");
        }
        if (!StringUtils.isNumeric((CharSequence)configuration.getMaximumSize())) {
            errors.rejectValue("maximumSize", "invalid");
        }
        if (!StringUtils.isNumeric((CharSequence)configuration.getTimeoutInSec())) {
            errors.rejectValue("timeoutInSec", "invalid");
        }
        if (!ConnectionPoolPropertyUtil.isValidProtocol((String)configuration.getSupportedProtocol())) {
            errors.rejectValue("supportedProtocol", "invalid");
        }
        if (!ConnectionPoolPropertyUtil.isValidAuthentication((String)configuration.getSupportedAuthentication())) {
            errors.rejectValue("supportedAuthentication", "invalid");
        }
    }
}

