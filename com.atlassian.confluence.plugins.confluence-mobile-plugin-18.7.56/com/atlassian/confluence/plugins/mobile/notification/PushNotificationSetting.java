/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.confluence.plugins.mobile.notification;

import com.atlassian.confluence.plugins.mobile.notification.NotificationCategory;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonValue;

public class PushNotificationSetting {
    @JsonProperty
    private Group group;
    @JsonProperty
    private Map<NotificationCategory, Boolean> customSettings;

    public PushNotificationSetting(@JsonProperty(value="group") Group group, @JsonProperty(value="customSettings") Map<NotificationCategory, Boolean> customSettings) {
        this.group = group;
        this.customSettings = customSettings;
    }

    public Group getGroup() {
        return this.group;
    }

    public Map<NotificationCategory, Boolean> getCustomSettings() {
        return this.customSettings;
    }

    public static enum Group {
        STANDARD("standard"),
        QUIET("quiet"),
        CUSTOM("custom"),
        NONE("none");

        private static Map<String, Group> groupMap;
        private String name;

        private Group(String name) {
            this.name = name;
        }

        @JsonValue
        public String getName() {
            return this.name;
        }

        @JsonCreator
        public static Group toValue(String name) {
            return groupMap.get(name);
        }

        static {
            groupMap = ImmutableMap.of((Object)STANDARD.getName(), (Object)((Object)STANDARD), (Object)QUIET.getName(), (Object)((Object)QUIET), (Object)CUSTOM.getName(), (Object)((Object)CUSTOM), (Object)NONE.getName(), (Object)((Object)NONE));
        }
    }
}

