/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.attribute.AttributeDefinition
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.business.insights.confluence.attribute;

import com.atlassian.business.insights.attribute.AttributeDefinition;
import com.atlassian.business.insights.confluence.attribute.SharedAttributes;
import com.google.common.collect.ImmutableList;
import java.util.List;

public final class AfcEventAttributes {
    public static final AttributeDefinition ID_ATTR = new AttributeDefinition("id", "event_id");
    public static final AttributeDefinition NAME_ATTR = new AttributeDefinition("name", "event_name");
    public static final AttributeDefinition AUTHOR_ATTR = new AttributeDefinition("author", "event_author_id");
    public static final AttributeDefinition SPACE_KEY_ATTR = new AttributeDefinition("spaceKey", "event_space_key");
    public static final AttributeDefinition CONTAINER_ID_ATTR = new AttributeDefinition("containerId", "event_container_id");
    public static final AttributeDefinition CONTENT_ID_ATTR = new AttributeDefinition("contentId", "event_content_id");

    private AfcEventAttributes() {
    }

    public static List<AttributeDefinition> getAttributes() {
        return ImmutableList.of((Object)SharedAttributes.INSTANCE_URL, (Object)ID_ATTR, (Object)NAME_ATTR, (Object)SharedAttributes.CREATED_DATE, (Object)AUTHOR_ATTR, (Object)SPACE_KEY_ATTR, (Object)CONTAINER_ID_ATTR, (Object)CONTENT_ID_ATTR);
    }
}

