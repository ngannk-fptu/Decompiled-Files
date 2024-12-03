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

public final class SpaceAttributes {
    public static final AttributeDefinition SPACE_NAME_ATTR = new AttributeDefinition("spaceName", "space_name");
    public static final AttributeDefinition HOMEPAGE_URL_ATTR = new AttributeDefinition("homepageUrl", "homepage_url");
    public static final AttributeDefinition SPACE_KEY_ATTR = new AttributeDefinition("spaceKey", "space_key");
    public static final AttributeDefinition SPACE_TYPE_ATTR = new AttributeDefinition("spaceType", "space_type");
    public static final AttributeDefinition SPACE_STATUS_ATTR = new AttributeDefinition("spaceStatus", "space_status");
    public static final AttributeDefinition SPACE_URL_ATTR = new AttributeDefinition("spaceUrl", "space_url");

    private SpaceAttributes() {
    }

    public static List<AttributeDefinition> getAttributes() {
        return ImmutableList.of((Object)SPACE_KEY_ATTR, (Object)SharedAttributes.INSTANCE_URL, (Object)SPACE_URL_ATTR, (Object)HOMEPAGE_URL_ATTR, (Object)SPACE_NAME_ATTR, (Object)SPACE_TYPE_ATTR, (Object)SPACE_STATUS_ATTR, (Object)SharedAttributes.CREATOR_ID, (Object)SharedAttributes.LAST_MODIFIER_ID, (Object)SharedAttributes.CREATED_DATE, (Object)SharedAttributes.UPDATED_DATE);
    }
}

