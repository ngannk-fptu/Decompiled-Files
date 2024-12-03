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

public final class UserAttributes {
    public static final AttributeDefinition ID_ATTR = new AttributeDefinition("id", "user_id");
    public static final AttributeDefinition NAME_ATTR = new AttributeDefinition("name", "user_name");
    public static final AttributeDefinition FULL_NAME_ATTR = new AttributeDefinition("fullname", "user_fullname");
    public static final AttributeDefinition EMAIL_ATTR = new AttributeDefinition("email", "user_email");

    private UserAttributes() {
    }

    public static List<AttributeDefinition> getAttributes() {
        return ImmutableList.of((Object)SharedAttributes.INSTANCE_URL, (Object)ID_ATTR, (Object)NAME_ATTR, (Object)FULL_NAME_ATTR, (Object)EMAIL_ATTR);
    }
}

