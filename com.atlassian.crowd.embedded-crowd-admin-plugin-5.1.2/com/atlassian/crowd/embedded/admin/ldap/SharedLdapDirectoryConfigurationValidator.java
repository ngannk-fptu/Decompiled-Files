/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.ldap.LdapPoolType
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.springframework.validation.Errors
 *  org.springframework.validation.Validator
 */
package com.atlassian.crowd.embedded.admin.ldap;

import com.atlassian.crowd.directory.ldap.LdapPoolType;
import com.atlassian.crowd.embedded.admin.ldap.SharedLdapDirectoryConfiguration;
import com.atlassian.crowd.embedded.admin.util.UrlValidationUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public abstract class SharedLdapDirectoryConfigurationValidator<T extends SharedLdapDirectoryConfiguration>
implements Validator {
    public final void validate(Object target, Errors errors) {
        SharedLdapDirectoryConfiguration configuration = (SharedLdapDirectoryConfiguration)target;
        this.validateConfiguration(configuration, errors);
        if (!(errors.hasFieldErrors("hostname") || errors.hasFieldErrors("port") || UrlValidationUtil.isValidUrl(configuration.getLdapUrl()))) {
            errors.rejectValue("hostname", "invalid");
        }
        if (LdapPoolType.COMMONS_POOL2.name().equalsIgnoreCase(configuration.getLdapPoolType())) {
            if (NumberUtils.toLong((String)configuration.getMinEvictableIdleTimeSeconds()) <= 0L) {
                errors.rejectValue("minEvictableIdleTimeSeconds", "invalid");
            }
            if (NumberUtils.toLong((String)configuration.getTimeBetweenEvictionRunsSeconds()) <= 0L) {
                errors.rejectValue("timeBetweenEvictionRunsSeconds", "invalid");
            }
        }
    }

    protected abstract void validateConfiguration(T var1, Errors var2);
}

