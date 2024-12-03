/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.seraph.filter;

import com.atlassian.seraph.filter.PasswordBasedLoginFilter;
import com.atlassian.seraph.util.SecurityUtils;
import javax.servlet.http.HttpServletRequest;

public class HttpAuthFilter
extends PasswordBasedLoginFilter {
    @Override
    protected PasswordBasedLoginFilter.UserPasswordPair extractUserPasswordPair(HttpServletRequest request) {
        SecurityUtils.UserPassCredentials creds;
        String auth = request.getHeader("Authorization");
        if (SecurityUtils.isBasicAuthorizationHeader(auth) && !"".equals((creds = SecurityUtils.decodeBasicAuthorizationCredentials(auth)).getUsername())) {
            return new PasswordBasedLoginFilter.UserPasswordPair(creds.getUsername(), creds.getPassword(), false);
        }
        return null;
    }
}

