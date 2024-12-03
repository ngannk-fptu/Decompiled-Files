/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.MobileUtils
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.plugins.mobile.event;

import com.atlassian.confluence.util.MobileUtils;
import javax.servlet.http.HttpServletRequest;

public abstract class MobileEvent {
    protected static final String PREFIX = "confluence.mobile.";
    private final HttpServletRequest request;

    public MobileEvent(HttpServletRequest request) {
        this.request = request;
    }

    public String getAgent() {
        return MobileUtils.getMobileOS((HttpServletRequest)this.request).getValue();
    }
}

