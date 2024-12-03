/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.base.events;

import com.opensymphony.oscache.base.events.CacheEvent;
import com.opensymphony.oscache.base.events.ScopeEventType;
import java.util.Date;

public final class ScopeEvent
extends CacheEvent {
    private Date date = null;
    private ScopeEventType eventType = null;
    private int scope = 0;

    public ScopeEvent(ScopeEventType eventType, int scope, Date date) {
        this(eventType, scope, date, null);
    }

    public ScopeEvent(ScopeEventType eventType, int scope, Date date, String origin) {
        super(origin);
        this.eventType = eventType;
        this.scope = scope;
        this.date = date;
    }

    public Date getDate() {
        return this.date;
    }

    public ScopeEventType getEventType() {
        return this.eventType;
    }

    public int getScope() {
        return this.scope;
    }
}

