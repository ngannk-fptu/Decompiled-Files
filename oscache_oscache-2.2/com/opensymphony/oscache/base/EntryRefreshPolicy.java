/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.base;

import com.opensymphony.oscache.base.CacheEntry;
import java.io.Serializable;

public interface EntryRefreshPolicy
extends Serializable {
    public boolean needsRefresh(CacheEntry var1);
}

