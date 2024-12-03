/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.web.api.provider;

import com.atlassian.plugin.web.api.WebItem;
import java.util.Map;

public interface WebItemProvider {
    public Iterable<WebItem> getItems(Map<String, Object> var1);
}

