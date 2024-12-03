/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.mobile.helper;

public final class MobileConstant {
    public static final String PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-mobile-plugin";
    public static final String WORK_BOX_PLUGIN_KEY = "com.atlassian.mywork.mywork-confluence-host-plugin";
    public static final String LEGACY_SESSION_BEHAVIOR_FEATURE_KEY = "com.atlassian.confluence.mobile.legacy.session.behavior";
    public static final String EMPTY_CHARACTER = "";
    public static final String DOT_CHARACTER = ".";
    public static final String SPACE_CHARACTER = " ";
    public static final String HYPHEN_MINUS_CHARACTER = "-";
    public static final String COMMA_CHARACTER = ",";
    public static final String QUESTION_CHARACTER = "?";
    public static final String ASTERISK_CHARACTER = "*";

    private MobileConstant() {
    }

    public static final class ServerInfo {
        public static final String PUSH_NOTIFICATION_ENABLED = "push-notification-enabled";
        public static final String SESSION_TIMEOUT_FIX_ENABLED = "session-timeout-fix-enabled";
    }

    public static final class LoginInfo {
        public static final String INSTANCE_NAME = "instance-name";
        public static final String BASE_URL = "base-url";
    }

    public static final class PushNotification {
        public static final String MOBILE_SERVER_PUSH_NOTIFICATION_SERVICE = System.getProperty("MOBILE_SERVER_PUSH_NOTIFICATION", "https://mobile-server-push-notification.atlassian.com");
        public static final String PUSH_NOTIFICATION_STATUS_KEY = "com.atlassian.confluence.plugins.confluence-mobile-plugin:push-notification-status";
        public static final String PUSH_NOTIFICATION_STATUS_EVENT_PREFIX = "push.status.";
    }

    public static final class Notification {
        public static final String USER_NAME_METADATA = "username";
        public static final String TASK_ID_METADATA = "taskId";
        public static final String GROUP_NAME_METADATA = "groupName";
        public static final String REPLY_YOUR_COMMENT = "replyYourComment";
        public static final String COMMENT_ON_YOUR_PAGE = "commentOnYourPage";
        public static final String SHARE_MESSAGE = "message";
        public static final int MAX_MESSAGE_LENGTH = 255;
    }

    public static final class Expands {
        public static final String WATCHED = "watched";
        public static final String SPACE = "space";

        public static final class Metadata {
            public static final String CURRENT_USER = "currentUser";
            public static final String LIKES = "likes";
            public static final String RESTRICTIONS = "restrictions";
        }
    }

    public static final class MobileApp {
        public static final String USER_AGENT = "AtlassianMobileApp";
        public static final String CONFLUENCE_MOBILE_USER_AGENT = "Confluence/";
    }
}

