/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.InvalidRequestException;
import javax.servlet.http.HttpServletRequest;

@Deprecated
public interface RequestValidator {
    public void validate(HttpServletRequest var1) throws InvalidRequestException;
}

