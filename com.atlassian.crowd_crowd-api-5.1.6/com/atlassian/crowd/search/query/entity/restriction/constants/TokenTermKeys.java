/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.PropertyImpl
 */
package com.atlassian.crowd.search.query.entity.restriction.constants;

import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.PropertyImpl;

public class TokenTermKeys {
    public static final Property<String> NAME = new PropertyImpl("name", String.class);
    public static final Property<Long> LAST_ACCESSED_TIME = new PropertyImpl("lastAccessedTime", Long.class);
    public static final Property<Long> DIRECTORY_ID = new PropertyImpl("directoryId", Long.class);
    public static final Property<Long> RANDOM_NUMBER = new PropertyImpl("randomNumber", Long.class);
}

