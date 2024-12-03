/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.xsrf.XsrfRequestValidator
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.sal.core.csrf;

import com.atlassian.sal.api.xsrf.XsrfRequestValidator;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.sal.core.xsrf.XsrfRequestValidatorImpl;
import javax.servlet.http.HttpServletRequest;

public class CsrfRequestValidator
implements XsrfRequestValidator {
    private final XsrfRequestValidator xsrfRequestValidator;

    public CsrfRequestValidator(XsrfTokenValidator tokenValidator) {
        this.xsrfRequestValidator = new XsrfRequestValidatorImpl(tokenValidator);
    }

    public boolean validateRequestPassesXsrfChecks(HttpServletRequest request) {
        return this.xsrfRequestValidator.validateRequestPassesXsrfChecks(request);
    }
}

