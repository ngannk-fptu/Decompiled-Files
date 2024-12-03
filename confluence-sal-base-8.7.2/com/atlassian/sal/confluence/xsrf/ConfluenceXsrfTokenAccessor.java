/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  com.atlassian.xwork.XsrfTokenGenerator
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.sal.confluence.xsrf;

import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.xwork.XsrfTokenGenerator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ConfluenceXsrfTokenAccessor
implements XsrfTokenAccessor {
    private final XsrfTokenGenerator xsrfTokenGenerator;

    public ConfluenceXsrfTokenAccessor(XsrfTokenGenerator xsrfTokenGenerator) {
        this.xsrfTokenGenerator = xsrfTokenGenerator;
    }

    public String getXsrfToken(HttpServletRequest request, HttpServletResponse response, boolean create) {
        return this.xsrfTokenGenerator.getToken(request, create);
    }
}

