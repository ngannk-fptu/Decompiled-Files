/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  net.jcip.annotations.Immutable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.tasklist.notification;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.plugins.tasklist.TaskModfication;
import com.atlassian.confluence.plugins.tasklist.notification.api.TaskPayload;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.Immutable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@Immutable
public class SimpleTaskPayload
implements TaskPayload {
    private final Map<UserKey, Iterable<TaskModfication>> tasks;
    private final String originatingUserKey;
    private final ContentId contentId;

    @JsonCreator
    public SimpleTaskPayload(@JsonProperty(value="tasks") Map<UserKey, List<TaskModfication>> tasks, @JsonProperty(value="originatingUserKey") String originatingUserKey, @JsonProperty(value="contentId") ContentId contentId) {
        this.originatingUserKey = originatingUserKey;
        this.contentId = contentId;
        ImmutableMap.Builder taskBuilder = ImmutableMap.builder();
        for (Map.Entry<UserKey, List<TaskModfication>> entry : tasks.entrySet()) {
            taskBuilder.put((Object)entry.getKey(), (Object)ImmutableList.copyOf((Collection)entry.getValue()));
        }
        this.tasks = taskBuilder.build();
    }

    @Override
    public ContentId getContentId() {
        return this.contentId;
    }

    @Override
    public Map<UserKey, Iterable<TaskModfication>> getTasks() {
        return this.tasks;
    }

    public Maybe<String> getOriginatingUserKey() {
        return Option.option((Object)this.originatingUserKey);
    }
}

