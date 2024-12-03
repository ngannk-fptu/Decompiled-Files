/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.MaybeNot
 *  com.atlassian.confluence.notifications.PayloadTransformerTemplate
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ListMultimap
 */
package com.atlassian.confluence.plugins.tasklist.notification;

import com.atlassian.confluence.core.MaybeNot;
import com.atlassian.confluence.notifications.PayloadTransformerTemplate;
import com.atlassian.confluence.plugins.tasklist.TaskModfication;
import com.atlassian.confluence.plugins.tasklist.event.SendTaskEmailEvent;
import com.atlassian.confluence.plugins.tasklist.notification.SimpleTaskPayload;
import com.atlassian.confluence.plugins.tasklist.notification.api.TaskPayload;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import java.util.List;
import java.util.Map;

public class TaskNotificationPayloadTransformer
extends PayloadTransformerTemplate<SendTaskEmailEvent, TaskPayload> {
    private UserAccessor userAccessor;

    public TaskNotificationPayloadTransformer(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    protected Maybe<TaskPayload> checkedCreate(SendTaskEmailEvent sendTaskEmailEvent) {
        if (sendTaskEmailEvent.isSuppressNotifications()) {
            return Option.none();
        }
        ListMultimap<String, TaskModfication> tasks = sendTaskEmailEvent.getTasks();
        if (tasks.isEmpty()) {
            return MaybeNot.becauseOf((String)"Tasks list is empty", (Object[])new Object[0]);
        }
        ConfluenceUser author = sendTaskEmailEvent.getContent().getLastModifier();
        String authorKey = author == null ? null : author.getKey().getStringValue();
        ImmutableMap.Builder payloadBuilder = ImmutableMap.builder();
        for (String assigneeName : tasks.keySet()) {
            ConfluenceUser assignee = this.userAccessor.getUserByName(assigneeName);
            if (assignee == null) continue;
            payloadBuilder.put((Object)assignee.getKey(), (Object)tasks.get((Object)assigneeName));
        }
        return Option.some((Object)new SimpleTaskPayload((Map<UserKey, List<TaskModfication>>)payloadBuilder.build(), authorKey, sendTaskEmailEvent.getContent().getContentId()));
    }
}

