/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.plugins.mobile.analytic;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.plugins.mobile.analytic.MobileAgent;
import com.atlassian.event.api.AsynchronousPreferred;
import javax.servlet.http.HttpServletRequest;

@AsynchronousPreferred
@EventName(value="confluence.mobile.page.macro.unsupported")
public class MobileUnsupportedMacroAnalyticEvent {
    private static final String APP_TYPE = "app";
    private String macroName;
    private Long contentId;
    private MobileAgent mobileAgent;

    public MobileUnsupportedMacroAnalyticEvent(HttpServletRequest request, String macroName, Long contentId) {
        this.macroName = macroName;
        this.contentId = contentId;
        this.mobileAgent = new MobileAgent(request);
    }

    public String getAgent() {
        return this.mobileAgent.getAgent();
    }

    public String getType() {
        return APP_TYPE;
    }

    public String getMacroName() {
        return this.macroName;
    }

    public Long getContentId() {
        return this.contentId;
    }
}

