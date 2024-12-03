/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.apache.struts2.views.util;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.RequestUtils;

public class ResourceUtil {
    public static String getResourceBase(HttpServletRequest req) {
        String path = RequestUtils.getServletPath(req);
        if (path == null || "".equals(path)) {
            return "";
        }
        return path.substring(0, path.lastIndexOf(47));
    }
}

