/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserProfile
 */
package com.atlassian.plugin.notifications.api.medium;

import com.atlassian.sal.api.user.UserProfile;
import java.util.Map;

public interface Message {
    public static final String MESSAGE_ID = "messageId";
    public static final String ORIGINATING_USER = "originatingUser";
    public static final String METADATA = "messageMetadata";

    public String getMessageId();

    public String getSubject();

    public String getBody();

    public Map<String, Object> getMetadata();

    public UserProfile getOriginatingUser();
}

