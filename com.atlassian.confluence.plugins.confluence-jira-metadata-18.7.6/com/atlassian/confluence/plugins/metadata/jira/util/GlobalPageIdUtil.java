/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 */
package com.atlassian.confluence.plugins.metadata.jira.util;

import com.atlassian.applinks.api.ApplicationId;

public class GlobalPageIdUtil {
    public static String generateGlobalPageId(ApplicationId appId, long pageId) {
        return "appId=" + appId.get() + "&pageId=" + pageId;
    }

    public static Long getPageId(String globalId) {
        try {
            return Long.parseLong(globalId.split("&pageId=")[1]);
        }
        catch (Exception e) {
            return null;
        }
    }
}

