/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.base.events;

import com.opensymphony.oscache.base.events.CacheEventListener;
import com.opensymphony.oscache.base.events.CacheMapAccessEvent;

public interface CacheMapAccessEventListener
extends CacheEventListener {
    public void accessed(CacheMapAccessEvent var1);
}

