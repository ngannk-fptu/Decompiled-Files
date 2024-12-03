/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.crowd.integration.http.filter;

import javax.servlet.http.HttpServletRequest;

public interface AuthenticationUrlProvider {
    public String authenticationUrl(HttpServletRequest var1);
}

