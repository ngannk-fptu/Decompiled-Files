/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.attribute.AttributeDefinition
 */
package com.atlassian.business.insights.confluence.attribute;

import com.atlassian.business.insights.attribute.AttributeDefinition;

public final class SharedAttributes {
    public static final AttributeDefinition INSTANCE_URL = new AttributeDefinition("instanceUrl", "instance_url");
    public static final AttributeDefinition CREATOR_ID = new AttributeDefinition("creatorId", "creator_id");
    public static final AttributeDefinition CREATED_DATE = new AttributeDefinition("createdDate", "created_date");
    public static final AttributeDefinition LAST_MODIFIER_ID = new AttributeDefinition("lastModifierId", "last_modifier_id");
    public static final AttributeDefinition UPDATED_DATE = new AttributeDefinition("updatedDate", "updated_date");

    private SharedAttributes() {
    }
}

