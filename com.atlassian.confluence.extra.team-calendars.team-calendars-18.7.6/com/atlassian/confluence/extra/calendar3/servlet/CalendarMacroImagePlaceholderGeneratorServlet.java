/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.user.User
 *  com.atlassian.util.profiling.UtilTimerStack
 *  com.google.common.collect.Collections2
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.servlet;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarSummary;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.user.User;
import com.atlassian.util.profiling.UtilTimerStack;
import com.google.common.collect.Collections2;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedHashSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalendarMacroImagePlaceholderGeneratorServlet
extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(CalendarMacroImagePlaceholderGeneratorServlet.class);
    private final CalendarManager calendarManager;
    private final LocaleManager localeManager;

    public CalendarMacroImagePlaceholderGeneratorServlet(CalendarManager calendarManager, LocaleManager localeManager) {
        this.calendarManager = calendarManager;
        this.localeManager = localeManager;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String methodSignature = "doGet(HttpServletRequest req, HttpServletResponse resp)";
        UtilTimerStack.push((String)methodSignature);
        try {
            String redirectUrl = String.format("%s/plugins/servlet/confluence/placeholder/macro?definition=%s&locale=%s&version=2&view=%s", req.getContextPath(), Base64.encodeBase64URLSafeString(this.getMacroDefinition(req).getBytes(Charset.forName("UTF-8"))), GeneralUtil.urlEncode((String)this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()).toString()), GeneralUtil.urlEncode((String)req.getParameter("defaultView")));
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Redirecting to %s", redirectUrl));
            }
            resp.sendRedirect(redirectUrl);
        }
        finally {
            UtilTimerStack.pop((String)methodSignature);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String getMacroDefinition(HttpServletRequest req) {
        String methodSignature = "getMacroDefinition(HttpServletRequest req)";
        UtilTimerStack.push((String)methodSignature);
        try {
            String subCalendarIds = req.getParameter("subCalendarIds");
            LinkedHashSet subCalendarNames = new LinkedHashSet();
            if (StringUtils.isNotBlank(subCalendarIds)) {
                subCalendarNames.addAll(Collections2.transform(Arrays.asList(StringUtils.split(subCalendarIds, ",")), subCalendarId -> {
                    SubCalendarSummary subCalendarSummary = this.calendarManager.getSubCalendarSummary((String)subCalendarId);
                    return null == subCalendarSummary ? subCalendarId : subCalendarSummary.getName();
                }));
            }
            StringBuilder macroDefinitionBuilder = new StringBuilder("{calendar");
            if (!subCalendarNames.isEmpty()) {
                macroDefinitionBuilder.append(":").append(StringUtils.join(subCalendarNames, ", "));
            }
            String string = macroDefinitionBuilder.append("}").toString();
            return string;
        }
        finally {
            UtilTimerStack.pop((String)methodSignature);
        }
    }
}

