/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.web.api;

import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.api.WebItem;
import com.atlassian.plugin.web.api.WebSection;
import java.util.Map;

public interface DynamicWebInterfaceManager
extends WebInterfaceManager {
    public Iterable<WebItem> getWebItems(String var1, Map<String, Object> var2);

    public Iterable<WebItem> getDisplayableWebItems(String var1, Map<String, Object> var2);

    public Iterable<WebSection> getWebSections(String var1, Map<String, Object> var2);

    public Iterable<WebSection> getDisplayableWebSections(String var1, Map<String, Object> var2);
}

