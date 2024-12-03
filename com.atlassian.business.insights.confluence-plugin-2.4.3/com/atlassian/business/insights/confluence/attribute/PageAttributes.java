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

public final class PageAttributes {
    public static final AttributeDefinition PAGE_ID_ATTR = new AttributeDefinition("page_id", "page_id");
    public static final AttributeDefinition LABELS_ATTR = new AttributeDefinition("labels", "labels");
    public static final AttributeDefinition SPACE_KEY_ATTR = new AttributeDefinition("space_key", "space_key");
    public static final AttributeDefinition LAST_UPDATE_DESCRIPTION_ATTR = new AttributeDefinition("last_update_description", "last_update_description");
    public static final AttributeDefinition PAGE_VERSION_ATTR = new AttributeDefinition("page_version", "page_version");
    public static final AttributeDefinition PAGE_TYPE_ATTR = new AttributeDefinition("page_type", "page_type");
    public static final AttributeDefinition PAGE_STATUS_ATTR = new AttributeDefinition("page_status", "page_status");
    public static final AttributeDefinition PAGE_URL_ATTR = new AttributeDefinition("page_url", "page_url");
    public static final AttributeDefinition PAGE_TITLE_ATTR = new AttributeDefinition("page_title", "page_title");
    public static final AttributeDefinition PAGE_CONTENT_ATTR = new AttributeDefinition("page_content", "page_content");
    public static final AttributeDefinition PAGE_PARENT_ID_ATTR = new AttributeDefinition("page_parent_id", "page_parent_id");

    private PageAttributes() {
    }

    public static List<AttributeDefinition> getAttributes() {
        return ImmutableList.of((Object)PAGE_ID_ATTR, (Object)SharedAttributes.INSTANCE_URL, (Object)SPACE_KEY_ATTR, (Object)PAGE_URL_ATTR, (Object)PAGE_TYPE_ATTR, (Object)PAGE_TITLE_ATTR, (Object)PAGE_STATUS_ATTR, (Object)PAGE_CONTENT_ATTR, (Object)PAGE_PARENT_ID_ATTR, (Object)LABELS_ATTR, (Object)PAGE_VERSION_ATTR, (Object)SharedAttributes.CREATOR_ID, (Object[])new AttributeDefinition[]{SharedAttributes.LAST_MODIFIER_ID, SharedAttributes.CREATED_DATE, SharedAttributes.UPDATED_DATE, LAST_UPDATE_DESCRIPTION_ATTR});
    }
}

