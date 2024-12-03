/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.plugin.notifications.spi;

import com.atlassian.plugin.notifications.api.ErrorCollection;
import com.atlassian.plugin.notifications.api.event.NotificationEvent;
import com.atlassian.plugin.notifications.api.notification.FilterConfiguration;
import com.atlassian.sal.api.message.I18nResolver;
import java.util.Map;

public interface NotificationFilterProvider {
    public Iterable<String> getWebResourcesToRequire();

    public String getConfigurationHtml(FilterConfiguration var1);

    public ErrorCollection validateFilter(I18nResolver var1, Map<String, String> var2);

    public boolean matchesFilter(NotificationEvent var1, FilterConfiguration var2);

    public String getSummary(FilterConfiguration var1);
}

