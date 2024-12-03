/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 */
package com.atlassian.confluence.plugins.emailtracker;

import com.atlassian.confluence.plugins.emailtracker.EmailTrackerService;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import java.util.HashMap;
import java.util.Map;

public class EmailTrackerContextProvider
implements ContextProvider {
    private final EmailTrackerService trackerService;

    public EmailTrackerContextProvider(EmailTrackerService trackerService) {
        this.trackerService = trackerService;
    }

    public void init(Map<String, String> stringStringMap) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> context) {
        String trackingUrl = this.trackerService.makeTrackingUrl(new HashMap<String, Object>(context));
        context.put("trackingUrl", trackingUrl);
        return context;
    }
}

