/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.ActivityObjectType
 *  com.atlassian.streams.api.ActivityObjectTypes
 *  com.atlassian.streams.api.ActivityObjectTypes$TypeFactory
 */
package com.atlassian.streams.confluence;

import com.atlassian.streams.api.ActivityObjectType;
import com.atlassian.streams.api.ActivityObjectTypes;

public final class ConfluenceActivityObjectTypes {
    private static final ActivityObjectTypes.TypeFactory confluenceTypes = ActivityObjectTypes.newTypeFactory((String)"http://streams.atlassian.com/syndication/types/");

    public static ActivityObjectType mail() {
        return confluenceTypes.newType("mail");
    }

    public static ActivityObjectType page() {
        return confluenceTypes.newType("page");
    }

    public static ActivityObjectType space() {
        return confluenceTypes.newType("space");
    }

    public static ActivityObjectType personalSpace() {
        return confluenceTypes.newType("personal-space", ConfluenceActivityObjectTypes.space());
    }
}

