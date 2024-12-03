/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.jira;

final class SeraphUtils {
    private SeraphUtils() {
    }

    static boolean isUserNamePasswordProvided(String url) {
        String lowerUrl = url.toLowerCase();
        return lowerUrl.contains("os_username") && lowerUrl.contains("os_password");
    }
}

