/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  com.atlassian.security.utils.ConstantTimeComparison
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.sal.core.xsrf;

import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.security.utils.ConstantTimeComparison;
import javax.servlet.http.HttpServletRequest;

public class IndependentXsrfTokenValidator
implements XsrfTokenValidator {
    public static final String XSRF_PARAM_NAME = "atl_token";
    private XsrfTokenAccessor accessor;

    public IndependentXsrfTokenValidator(XsrfTokenAccessor accessor) {
        this.accessor = accessor;
    }

    public boolean validateFormEncodedToken(HttpServletRequest request) {
        String parameterToken = request.getParameter(XSRF_PARAM_NAME);
        String requestToken = this.accessor.getXsrfToken(request, null, false);
        return parameterToken != null && requestToken != null && ConstantTimeComparison.isEqual((String)parameterToken, (String)requestToken);
    }

    public String getXsrfParameterName() {
        return XSRF_PARAM_NAME;
    }
}

