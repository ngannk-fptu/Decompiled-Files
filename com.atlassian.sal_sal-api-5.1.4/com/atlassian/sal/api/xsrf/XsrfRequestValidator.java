/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.sal.api.xsrf;

import javax.servlet.http.HttpServletRequest;

public interface XsrfRequestValidator {
    public boolean validateRequestPassesXsrfChecks(HttpServletRequest var1);
}

