/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.interceptor.csp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CspSettings {
    public static final int NONCE_RANDOM_LENGTH = 18;
    public static final String CSP_ENFORCE_HEADER = "Content-Security-Policy";
    public static final String CSP_REPORT_HEADER = "Content-Security-Policy-Report-Only";
    public static final String OBJECT_SRC = "object-src";
    public static final String SCRIPT_SRC = "script-src";
    public static final String BASE_URI = "base-uri";
    public static final String REPORT_URI = "report-uri";
    public static final String NONE = "none";
    public static final String STRICT_DYNAMIC = "strict-dynamic";
    public static final String HTTP = "http:";
    public static final String HTTPS = "https:";
    public static final String CSP_REPORT_TYPE = "application/csp-report";

    @Deprecated
    public void addCspHeaders(HttpServletResponse var1);

    public void addCspHeaders(HttpServletRequest var1, HttpServletResponse var2);

    public void setReportUri(String var1);

    public void setEnforcingMode(boolean var1);
}

