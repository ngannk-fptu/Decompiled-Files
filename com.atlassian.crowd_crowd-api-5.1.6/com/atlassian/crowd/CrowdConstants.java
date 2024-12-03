/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd;

public class CrowdConstants {
    public static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";
    public static final String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";

    private CrowdConstants() {
    }

    public static class CrowdHome {
        @Deprecated
        public static final String PLUGIN_DATA_LOCATION = "plugin-data";
        public static final String LOG_FILE_LOCATION = "logs";
        public static final String BACKUPS_LOCATION = "backups";
        public static final String PLUGINS_LOCATION = "plugins";

        private CrowdHome() {
        }
    }
}

