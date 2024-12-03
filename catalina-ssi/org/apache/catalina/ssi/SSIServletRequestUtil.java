/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.tomcat.util.http.RequestUtil
 */
package org.apache.catalina.ssi;

import javax.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.RequestUtil;

public class SSIServletRequestUtil {
    public static String getRelativePath(HttpServletRequest request) {
        if (request.getAttribute("javax.servlet.include.request_uri") != null) {
            String result = (String)request.getAttribute("javax.servlet.include.path_info");
            if (result == null) {
                result = (String)request.getAttribute("javax.servlet.include.servlet_path");
            }
            if (result == null || result.equals("")) {
                result = "/";
            }
            return result;
        }
        String result = request.getPathInfo();
        if (result == null) {
            result = request.getServletPath();
        }
        if (result == null || result.equals("")) {
            result = "/";
        }
        return RequestUtil.normalize((String)result);
    }
}

