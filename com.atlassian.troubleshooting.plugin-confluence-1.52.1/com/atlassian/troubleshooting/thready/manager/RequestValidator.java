/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.troubleshooting.thready.manager;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class RequestValidator {
    public boolean isResourceRequest(HttpServletRequest request) {
        return StringUtils.endsWithAny((CharSequence)request.getRequestURI(), (CharSequence[])new CharSequence[]{".css", ".js", ".png", ".jpg", ".ico", ".woff", ".gif", ".svg"});
    }
}

