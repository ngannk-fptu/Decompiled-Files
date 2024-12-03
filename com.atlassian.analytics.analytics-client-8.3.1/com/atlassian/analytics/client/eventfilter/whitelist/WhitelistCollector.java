/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.eventfilter.whitelist;

import com.atlassian.analytics.client.eventfilter.whitelist.Whitelist;
import java.util.List;

public interface WhitelistCollector {
    public List<Whitelist> collectExternalWhitelists();
}

