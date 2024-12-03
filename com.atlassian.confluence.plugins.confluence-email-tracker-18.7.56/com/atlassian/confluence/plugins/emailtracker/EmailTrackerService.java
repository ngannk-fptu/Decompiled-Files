/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.emailtracker;

import com.atlassian.confluence.plugins.emailtracker.InvalidTrackingRequestException;
import java.util.Map;

public interface EmailTrackerService {
    public static final String TIMESTAMP = "timestamp";
    public static final String RECIPIENT_KEY = "recipientKey";
    public static final String ACTOR_KEY = "actorKey";
    public static final String ACTION = "action";
    public static final String CONTENT_ID = "contentId";

    public void handleTrackingRequest(String var1, Map<String, String> var2) throws InvalidTrackingRequestException;

    public String makeTrackingUrl(Map<String, Object> var1);
}

