/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.BaseApiEnum
 *  com.google.common.collect.ImmutableList
 *  org.codehaus.jackson.annotate.JsonCreator
 */
package com.atlassian.confluence.plugins.mobile.model.card;

import com.atlassian.confluence.api.model.BaseApiEnum;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.codehaus.jackson.annotate.JsonCreator;

public final class ActivityType
extends BaseApiEnum {
    public static final ActivityType CREATE = new ActivityType("create");
    public static final ActivityType EDIT = new ActivityType("edit");
    public static final ActivityType COMMENT = new ActivityType("comment");
    public static final ActivityType MENTION = new ActivityType("mention");
    public static final ActivityType SHARE = new ActivityType("share");
    public static final ActivityType VIEW = new ActivityType("view");
    public static final List<ActivityType> BUILT_IN = ImmutableList.of((Object)((Object)CREATE), (Object)((Object)EDIT), (Object)((Object)COMMENT), (Object)((Object)MENTION), (Object)((Object)SHARE), (Object)((Object)VIEW));

    public ActivityType(String value) {
        super(value);
    }

    @JsonCreator
    public static ActivityType valueOf(String type) {
        for (ActivityType activityType : BUILT_IN) {
            if (!type.equals(activityType.getType())) continue;
            return activityType;
        }
        return new ActivityType(type);
    }

    public String getType() {
        return this.serialise();
    }
}

