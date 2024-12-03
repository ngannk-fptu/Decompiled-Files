/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  javax.servlet.http.HttpServletRequest
 */
package com.benryan.conversion;

import com.atlassian.confluence.pages.AbstractPage;
import javax.servlet.http.HttpServletRequest;

public class WebDavUtil {
    private String pageWebDavPath;

    public WebDavUtil(AbstractPage page) {
        this.pageWebDavPath = "/" + page.getId() + "/";
    }

    public String buildConfluenceUrl(HttpServletRequest req) {
        String scheme = req.getScheme();
        String url = scheme + "://" + req.getServerName();
        if (scheme.equals("http") && req.getServerPort() != 80 || scheme.equals("https") && req.getServerPort() != 443) {
            url = url + ":" + req.getServerPort();
        }
        return url + req.getContextPath();
    }

    public String getBaseRelWebDavUrl() {
        return "/" + this.getWebDavPath();
    }

    public String getRelWebDavUrl(String fileName) {
        return this.getBaseRelWebDavUrl() + this.pageWebDavPath + "attachments/" + fileName;
    }

    public String getWebDavPath() {
        return "plugins/servlet/confluence/editinword";
    }
}

