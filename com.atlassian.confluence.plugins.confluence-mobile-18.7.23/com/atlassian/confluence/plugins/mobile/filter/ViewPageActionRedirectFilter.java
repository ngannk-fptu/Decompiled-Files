/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  com.atlassian.event.api.EventPublisher
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.mobile.filter;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.mobile.MobileUtils;
import com.atlassian.confluence.plugins.mobile.event.MobileViewRedirectEvent;
import com.atlassian.core.filters.AbstractHttpFilter;
import com.atlassian.event.api.EventPublisher;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

public class ViewPageActionRedirectFilter
extends AbstractHttpFilter {
    private final PageManager pageManager;
    private String redirectPrefix;
    private EventPublisher eventPublisher;

    public ViewPageActionRedirectFilter(PageManager pageManager, EventPublisher eventPublisher) {
        this.pageManager = pageManager;
        this.eventPublisher = eventPublisher;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        this.redirectPrefix = filterConfig.getInitParameter("redirectPrefix");
    }

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (!MobileUtils.isMobileViewRequest(request)) {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
            return;
        }
        this.eventPublisher.publish((Object)new MobileViewRedirectEvent(request));
        response.sendRedirect(this.buildRedirectUrl(request));
    }

    private String buildRedirectUrl(HttpServletRequest request) {
        long contentId = this.getContentIdFromRequest(request);
        String requestParam = "?contentId=" + contentId;
        String requestFragment = this.redirectPrefix + contentId;
        String redirectUrl = request.getContextPath() + "/plugins/servlet/mobile";
        String commentId = request.getParameter("focusedCommentId");
        if (StringUtils.isNotBlank((CharSequence)commentId)) {
            requestFragment = requestFragment + "/" + commentId;
            requestParam = requestParam + "&commentId=" + commentId;
        }
        return redirectUrl + requestParam + requestFragment;
    }

    private long getContentIdFromRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        Page result = null;
        if (requestURI.endsWith("viewpage.action")) {
            String pageIdStr = request.getParameter("pageId");
            if (StringUtils.isNotBlank((CharSequence)pageIdStr)) {
                return Long.parseLong(pageIdStr);
            }
            String postingDay = request.getParameter("postingDay");
            String spaceKey = request.getParameter("spaceKey");
            String title = request.getParameter("title");
            if (StringUtils.isNotBlank((CharSequence)postingDay) && StringUtils.isNotBlank((CharSequence)spaceKey) && StringUtils.isNotBlank((CharSequence)title)) {
                SimpleDateFormat postingDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                try {
                    Calendar postingDate = Calendar.getInstance();
                    postingDate.setTime(postingDateFormat.parse(postingDay));
                    result = this.pageManager.getBlogPost(spaceKey, title, postingDate);
                }
                catch (ParseException parseException) {}
            } else if (StringUtils.isNotBlank((CharSequence)spaceKey) && StringUtils.isNotBlank((CharSequence)title)) {
                result = this.pageManager.getPage(spaceKey, title);
            }
        }
        if (result != null && result instanceof ContentEntityObject) {
            return ((ContentEntityObject)result).getId();
        }
        return -1L;
    }
}

