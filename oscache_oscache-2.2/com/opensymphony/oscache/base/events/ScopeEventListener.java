/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.base.events;

import com.opensymphony.oscache.base.events.CacheEventListener;
import com.opensymphony.oscache.base.events.ScopeEvent;

public interface ScopeEventListener
extends CacheEventListener {
    public void scopeFlushed(ScopeEvent var1);
}

