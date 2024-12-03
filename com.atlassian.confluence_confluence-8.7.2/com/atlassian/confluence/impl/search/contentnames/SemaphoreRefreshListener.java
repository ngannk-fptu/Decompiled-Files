/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 *  com.atlassian.event.EventListener
 */
package com.atlassian.confluence.impl.search.contentnames;

import com.atlassian.confluence.event.events.admin.GlobalSettingsChangedEvent;
import com.atlassian.confluence.impl.search.contentnames.SemaphoreHolder;
import com.atlassian.event.Event;
import com.atlassian.event.EventListener;

public class SemaphoreRefreshListener
implements EventListener {
    private SemaphoreHolder contentNameSearchSemaphoreHolder;

    public void handleEvent(Event event) {
        int newMax;
        GlobalSettingsChangedEvent settingsChangedEvent = (GlobalSettingsChangedEvent)event;
        int oldMax = settingsChangedEvent.getOldSettings().getMaxSimultaneousQuickNavRequests();
        if (oldMax != (newMax = settingsChangedEvent.getNewSettings().getMaxSimultaneousQuickNavRequests())) {
            this.contentNameSearchSemaphoreHolder.refreshSemaphore();
        }
    }

    public Class[] getHandledEventClasses() {
        return new Class[]{GlobalSettingsChangedEvent.class};
    }

    public void setContentNameSearchSemaphoreHolder(SemaphoreHolder contentNameSearchSemaphoreHolder) {
        this.contentNameSearchSemaphoreHolder = contentNameSearchSemaphoreHolder;
    }
}

