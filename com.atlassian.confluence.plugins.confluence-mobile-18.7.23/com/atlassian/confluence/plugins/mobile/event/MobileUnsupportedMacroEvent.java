/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.plugins.mobile.event;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.plugins.mobile.event.MobileEvent;
import javax.servlet.http.HttpServletRequest;

public class MobileUnsupportedMacroEvent
extends MobileEvent {
    public static final String WEB_TYPE = "web";
    private String macroName;
    private Long contentId;
    private String type;

    public MobileUnsupportedMacroEvent(HttpServletRequest request, Long contentId, String macroName, String type) {
        super(request);
        this.macroName = macroName;
        this.contentId = contentId;
        this.type = type;
    }

    @EventName
    public String getEvent() {
        return "confluence.mobile.page.macro.unsupported";
    }

    public String getMacroName() {
        return this.macroName;
    }

    public Long getContentId() {
        return this.contentId;
    }

    public String getType() {
        return this.type;
    }
}

