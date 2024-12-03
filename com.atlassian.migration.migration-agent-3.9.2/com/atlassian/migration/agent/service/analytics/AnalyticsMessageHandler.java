/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.analytics;

import com.atlassian.migration.agent.service.util.MaskingUtility;

public class AnalyticsMessageHandler {
    private static final int ALLOWED_LENGTH = 150;

    private AnalyticsMessageHandler() {
    }

    public static String messageHandler(String message) {
        if (message == null) {
            return "";
        }
        if ((message = MaskingUtility.mask(message)).length() < 150) {
            return message;
        }
        return message.substring(0, 150);
    }
}

