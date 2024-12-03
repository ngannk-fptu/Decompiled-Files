/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.xsrf.XsrfHeaderValidator
 *  com.atlassian.sal.api.xsrf.XsrfRequestValidator
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.sal.core.xsrf;

import com.atlassian.sal.api.xsrf.XsrfHeaderValidator;
import com.atlassian.sal.api.xsrf.XsrfRequestValidator;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import javax.servlet.http.HttpServletRequest;

public class XsrfRequestValidatorImpl
implements XsrfRequestValidator {
    private static final XsrfHeaderValidator headerValidator = new XsrfHeaderValidator();
    private final XsrfTokenValidator tokenValidator;

    public XsrfRequestValidatorImpl(XsrfTokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    public boolean validateRequestPassesXsrfChecks(HttpServletRequest request) {
        return headerValidator.requestHasValidXsrfHeader(request) || this.tokenValidator.validateFormEncodedToken(request);
    }
}

