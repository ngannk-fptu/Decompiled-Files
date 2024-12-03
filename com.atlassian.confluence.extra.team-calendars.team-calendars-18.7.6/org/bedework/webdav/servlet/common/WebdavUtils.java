/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.bedework.webdav.servlet.common;

import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.bedework.util.misc.Util;

public class WebdavUtils {
    public static String getUrlPrefix(HttpServletRequest req) {
        try {
            int pos;
            String prefix;
            String sp;
            String url = req.getRequestURL().toString();
            String contextPath = req.getContextPath();
            if (contextPath == null || contextPath.equals(".")) {
                contextPath = "/";
            }
            if ((sp = req.getServletPath()) == null || sp.equals(".")) {
                sp = "/";
            }
            if ((prefix = Util.buildPath(false, contextPath, "/", sp)).equals("/")) {
                prefix = "";
            }
            if ((pos = url.indexOf(prefix)) > 0) {
                url = url.substring(0, pos);
            }
            return url + prefix;
        }
        catch (Throwable t) {
            Logger.getLogger(WebdavUtils.class).warn("Unable to get url from " + req);
            return "BogusURL.this.is.probably.a.portal";
        }
    }

    public static boolean emptyCollection(Collection c) {
        return c == null || c.size() == 0;
    }
}

