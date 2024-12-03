/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.interceptor.MethodInvocation
 *  com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.business.insights.core.rest.validation;

import com.atlassian.business.insights.core.rest.exception.LicenseException;
import com.atlassian.business.insights.core.rest.validation.ValidationResult;
import com.atlassian.business.insights.core.service.LicenseChecker;
import com.atlassian.plugins.rest.common.interceptor.MethodInvocation;
import com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor;
import java.lang.reflect.InvocationTargetException;
import javax.ws.rs.ext.Provider;

@Provider
public class ValidateLicenseIsDc
implements ResourceInterceptor {
    private static final String BAD_REQUEST_MISSING_LICENSE = "data-pipeline.api.rest.dc.license.required";
    private final LicenseChecker licenseChecker;

    public ValidateLicenseIsDc(LicenseChecker licenseChecker) {
        this.licenseChecker = licenseChecker;
    }

    public void intercept(MethodInvocation methodInvocation) throws InvocationTargetException, IllegalAccessException {
        if (!this.licenseChecker.isDcLicense()) {
            throw new LicenseException(new ValidationResult(BAD_REQUEST_MISSING_LICENSE));
        }
        methodInvocation.invoke();
    }
}

