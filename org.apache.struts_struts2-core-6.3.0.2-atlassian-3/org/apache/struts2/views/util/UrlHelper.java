/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.views.util;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UrlHelper {
    public static final int DEFAULT_HTTP_PORT = 80;
    public static final int DEFAULT_HTTPS_PORT = 443;
    public static final String AMP = "&amp;";

    public String buildUrl(String var1, HttpServletRequest var2, HttpServletResponse var3, Map<String, Object> var4);

    public String buildUrl(String var1, HttpServletRequest var2, HttpServletResponse var3, Map<String, Object> var4, String var5, boolean var6, boolean var7);

    public String buildUrl(String var1, HttpServletRequest var2, HttpServletResponse var3, Map<String, Object> var4, String var5, boolean var6, boolean var7, boolean var8);

    public String buildUrl(String var1, HttpServletRequest var2, HttpServletResponse var3, Map<String, Object> var4, String var5, boolean var6, boolean var7, boolean var8, boolean var9);
}

