/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.seraph.config.SecurityConfig
 *  com.atlassian.seraph.util.RedirectUtils
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.seraph.config.SecurityConfig;
import com.atlassian.seraph.util.RedirectUtils;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class SeraphUtils {
    public static String getLoginURL(HttpServletRequest request) {
        return RedirectUtils.getLoginUrl((HttpServletRequest)request);
    }

    public static String getLinkLoginURL(HttpServletRequest request, String username) {
        String url = SeraphUtils.getLinkLoginURL(request);
        if (StringUtils.isBlank((CharSequence)username)) {
            return url;
        }
        return GeneralUtil.appendAmpersandOrQuestionMark(url) + "os_username=" + HtmlUtil.urlEncode(username);
    }

    public static String getLinkLoginURL(HttpServletRequest request) {
        Object linkLoginURL = RedirectUtils.getLinkLoginURL((HttpServletRequest)request);
        String currentPageUrl = request.getRequestURI() + (String)(StringUtils.isNotEmpty((CharSequence)request.getQueryString()) ? "?" + request.getQueryString() : "");
        if (currentPageUrl.contains("/logout.action") || currentPageUrl.contains("/login.action") || currentPageUrl.contains("/signup.action") || currentPageUrl.contains("/dosignup.action")) {
            return linkLoginURL;
        }
        Object destination = request.getParameter("atl_after_login_redirect");
        if (StringUtils.isBlank((CharSequence)destination)) {
            destination = currentPageUrl.contains("forgotuserpassword.action") || currentPageUrl.contains("confirmemail.action") ? "/homepage.action" : currentPageUrl;
        }
        destination = SeraphUtils.stripContextPathFromRequestURL(request, (String)destination);
        linkLoginURL = GeneralUtil.appendAmpersandOrQuestionMark((String)linkLoginURL);
        linkLoginURL = (String)linkLoginURL + "os_destination=" + HtmlUtil.urlEncode((String)destination);
        return linkLoginURL;
    }

    public static String getLinkLoginURLAndStripContextPath(HttpServletRequest request) {
        String linkLoginURL = SeraphUtils.getLinkLoginURL(request);
        return SeraphUtils.stripContextPathFromRequestURL(request, linkLoginURL);
    }

    public static String stripContextPathFromRequestURL(HttpServletRequest request, String currentPageUrl) {
        if (currentPageUrl.startsWith(request.getContextPath())) {
            currentPageUrl = currentPageUrl.substring(request.getContextPath().length());
        }
        return currentPageUrl;
    }

    public static String getOriginalURL(HttpServletRequest request) {
        String originalURL;
        SecurityConfig config = SeraphUtils.getConfig(request);
        if (config != null && StringUtils.isNotEmpty((CharSequence)(originalURL = (String)request.getSession().getAttribute(config.getOriginalURLKey())))) {
            return originalURL;
        }
        return request.getParameter("os_destination");
    }

    public static SecurityConfig getConfig(HttpServletRequest request) {
        return (SecurityConfig)request.getSession().getServletContext().getAttribute("seraph_config");
    }
}

