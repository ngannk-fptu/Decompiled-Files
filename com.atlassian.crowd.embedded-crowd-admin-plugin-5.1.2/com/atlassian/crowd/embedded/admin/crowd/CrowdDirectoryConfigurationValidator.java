/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.validation.Errors
 *  org.springframework.validation.Validator
 */
package com.atlassian.crowd.embedded.admin.crowd;

import com.atlassian.crowd.embedded.admin.crowd.CrowdDirectoryConfiguration;
import com.atlassian.crowd.embedded.admin.util.UrlValidationUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public final class CrowdDirectoryConfigurationValidator
implements Validator {
    public boolean supports(Class clazz) {
        return CrowdDirectoryConfiguration.class.isAssignableFrom(clazz);
    }

    public void validate(Object target, Errors errors) {
        CrowdDirectoryConfiguration configuration = (CrowdDirectoryConfiguration)target;
        if (!UrlValidationUtil.isValidUrl(configuration.getCrowdServerUrl())) {
            errors.rejectValue("crowdServerUrl", "invalid");
        }
        if (configuration.getCrowdServerSynchroniseIntervalInMin() < 1L) {
            errors.rejectValue("crowdServerSynchroniseIntervalInMin", "invalid");
        }
        if (StringUtils.isEmpty((CharSequence)configuration.getPassword()) && this.isCreatingNewDirectory(configuration)) {
            errors.rejectValue("applicationPassword", "required");
        }
        if (StringUtils.isEmpty((CharSequence)configuration.getHttpProxyHost()) && (StringUtils.isNotEmpty((CharSequence)configuration.getHttpProxyPort()) || StringUtils.isNotEmpty((CharSequence)configuration.getHttpProxyUsername()) || StringUtils.isNotEmpty((CharSequence)configuration.getHttpProxyPassword()))) {
            errors.rejectValue("httpProxyHost", "required");
        }
        if (StringUtils.isEmpty((CharSequence)configuration.getHttpProxyUsername()) && StringUtils.isNotEmpty((CharSequence)configuration.getHttpProxyPassword())) {
            errors.rejectValue("httpProxyUsername", "required");
        }
        if (StringUtils.isEmpty((CharSequence)configuration.getHttpProxyPassword()) && StringUtils.isNotEmpty((CharSequence)configuration.getHttpProxyUsername())) {
            errors.rejectValue("httpProxyPassword", "required");
        }
    }

    private boolean isCreatingNewDirectory(CrowdDirectoryConfiguration configuration) {
        return configuration.getDirectoryId() == 0L;
    }
}

