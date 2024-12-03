/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.crowd.search.query.entity.restriction.constants;

import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.PropertyImpl;
import com.google.common.collect.ImmutableSet;
import java.util.Date;
import java.util.Set;

public class GroupTermKeys {
    public static final Property<String> NAME = new PropertyImpl<String>("name", String.class);
    public static final Property<String> DESCRIPTION = new PropertyImpl<String>("description", String.class);
    public static final Property<Boolean> ACTIVE = new PropertyImpl<Boolean>("active", Boolean.class);
    public static final Property<Date> CREATED_DATE = new PropertyImpl<Date>("createdDate", Date.class);
    public static final Property<Date> UPDATED_DATE = new PropertyImpl<Date>("updatedDate", Date.class);
    public static final Property<Boolean> LOCAL = new PropertyImpl<Boolean>("local", Boolean.class);
    public static final Property<String> EXTERNAL_ID = new PropertyImpl<String>("externalId", String.class);
    public static final Set<Property<?>> ALL_GROUP_PROPERTIES = ImmutableSet.of(NAME, DESCRIPTION, ACTIVE, CREATED_DATE, UPDATED_DATE, LOCAL, (Object[])new Property[]{EXTERNAL_ID});

    private GroupTermKeys() {
    }
}

