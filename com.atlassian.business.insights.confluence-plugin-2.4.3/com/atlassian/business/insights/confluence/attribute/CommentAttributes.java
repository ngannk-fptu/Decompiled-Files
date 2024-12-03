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

public final class CommentAttributes {
    public static final AttributeDefinition ID_ATTR = new AttributeDefinition("id", "comment_id");
    public static final AttributeDefinition CONTENT_ATTR = new AttributeDefinition("content", "comment_content");
    public static final AttributeDefinition PARENT_COMMENT_ID_ATTR = new AttributeDefinition("parentCommentId", "parent_comment_id");
    public static final AttributeDefinition PARENT_PAGE_ID_ATTR = new AttributeDefinition("parentPageId", "page_id");
    public static final AttributeDefinition COMMENT_URL = new AttributeDefinition("commentUrl", "comment_url");
    public static final AttributeDefinition SPACE_KEY_ATTR = new AttributeDefinition("spaceKey", "space_key");

    private CommentAttributes() {
    }

    public static List<AttributeDefinition> getAttributes() {
        return ImmutableList.of((Object)ID_ATTR, (Object)SharedAttributes.INSTANCE_URL, (Object)COMMENT_URL, (Object)PARENT_PAGE_ID_ATTR, (Object)PARENT_COMMENT_ID_ATTR, (Object)CONTENT_ATTR, (Object)SharedAttributes.CREATOR_ID, (Object)SharedAttributes.LAST_MODIFIER_ID, (Object)SharedAttributes.CREATED_DATE, (Object)SharedAttributes.UPDATED_DATE);
    }
}

