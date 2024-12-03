/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  com.atlassian.xwork.XsrfTokenGenerator
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.sal.confluence.xsrf;

import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.xwork.XsrfTokenGenerator;
import javax.servlet.http.HttpServletRequest;

public class ConfluenceXsrfTokenValidator
implements XsrfTokenValidator {
    private final XsrfTokenGenerator xsrfTokenGenerator;

    public ConfluenceXsrfTokenValidator(XsrfTokenGenerator xsrfTokenGenerator) {
        this.xsrfTokenGenerator = xsrfTokenGenerator;
    }

    public boolean validateFormEncodedToken(HttpServletRequest request) {
        String parameterTokenValue = request.getParameter(this.getXsrfParameterName());
        return this.xsrfTokenGenerator.validateToken(request, parameterTokenValue);
    }

    public String getXsrfParameterName() {
        return this.xsrfTokenGenerator.getXsrfTokenName();
    }
}

