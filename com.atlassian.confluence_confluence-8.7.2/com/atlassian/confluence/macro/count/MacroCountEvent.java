/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.macro.count;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.google.common.base.Preconditions;

public class MacroCountEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = -5911779667563573375L;
    private final String contentId;
    private final String containerId;
    private final String renderContext;
    private final String outputDeviceType;
    private final String contentEntityType;
    private final int macroCount;
    private final String macroType;
    private final String eventName;

    MacroCountEvent(Object src, String contentId, String containerId, String renderContext, String outputDeviceType, String contentEntityType, int macroCount, String macroType, String eventName) {
        super(src);
        this.contentId = (String)Preconditions.checkNotNull((Object)contentId);
        this.containerId = (String)Preconditions.checkNotNull((Object)containerId);
        this.renderContext = (String)Preconditions.checkNotNull((Object)renderContext);
        this.outputDeviceType = (String)Preconditions.checkNotNull((Object)outputDeviceType);
        this.contentEntityType = (String)Preconditions.checkNotNull((Object)contentEntityType);
        this.macroType = (String)Preconditions.checkNotNull((Object)macroType);
        this.eventName = (String)Preconditions.checkNotNull((Object)eventName);
        this.macroCount = macroCount;
        Preconditions.checkArgument((macroCount >= 0 ? 1 : 0) != 0);
    }

    @EventName
    public String buildEventName() {
        return this.eventName;
    }

    public String getContentId() {
        return this.contentId;
    }

    public String getContainerId() {
        return this.containerId;
    }

    public Integer getCount() {
        return this.macroCount;
    }

    public String getOutputType() {
        return this.renderContext;
    }

    public String getOutputDeviceType() {
        return this.outputDeviceType;
    }

    public String getEntityType() {
        return this.contentEntityType;
    }

    public String getMacroType() {
        return this.macroType;
    }
}

