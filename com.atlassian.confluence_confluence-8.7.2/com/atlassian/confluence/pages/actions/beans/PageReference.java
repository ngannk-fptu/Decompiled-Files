/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.pages.actions.beans;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class PageReference {
    public static final String ATTRIBUTE_KEY = "pageReference";
    private final String spaceKey;
    private final String pageTitle;

    public static void set(HttpServletRequest request, String spaceKey, String pageTitle) {
        if (!StringUtils.isBlank((CharSequence)pageTitle) && !StringUtils.isBlank((CharSequence)spaceKey)) {
            request.setAttribute(ATTRIBUTE_KEY, (Object)new PageReference(spaceKey, pageTitle));
        }
    }

    public static PageReference get(HttpServletRequest request) {
        return (PageReference)request.getAttribute(ATTRIBUTE_KEY);
    }

    public PageReference(String spaceKey, String pageTitle) {
        this.spaceKey = spaceKey;
        this.pageTitle = pageTitle;
    }

    public String getPageTitle() {
        return this.pageTitle;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }
}

