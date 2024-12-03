/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.seraph.filter;

import com.atlassian.seraph.auth.AuthenticationErrorType;
import javax.servlet.http.HttpServletRequest;

public final class LoginFilterRequest {
    public static String getAuthenticationStatus(HttpServletRequest request) {
        Object authStatus = request.getAttribute("os_authstatus");
        if (authStatus == null) {
            return null;
        }
        if (authStatus instanceof String) {
            return (String)authStatus;
        }
        throw new IllegalStateException("Illegal Authentication Status " + authStatus);
    }

    public static AuthenticationErrorType getAuthenticationErrorType(HttpServletRequest request) {
        Object errorType = request.getAttribute("auth_error_type");
        if (errorType == null) {
            return null;
        }
        if (errorType instanceof AuthenticationErrorType) {
            return (AuthenticationErrorType)((Object)errorType);
        }
        throw new IllegalStateException("Illegal Authentication ErrorType " + errorType);
    }
}

