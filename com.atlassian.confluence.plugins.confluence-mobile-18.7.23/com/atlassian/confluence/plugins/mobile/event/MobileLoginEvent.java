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

public class MobileLoginEvent
extends MobileEvent {
    public MobileLoginEvent(HttpServletRequest request) {
        super(request);
    }

    @EventName
    public String getEvent() {
        return "confluence.mobile.login";
    }
}

