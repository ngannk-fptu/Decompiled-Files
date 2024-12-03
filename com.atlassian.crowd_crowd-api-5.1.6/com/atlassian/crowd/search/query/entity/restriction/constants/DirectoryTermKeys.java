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

public class DirectoryTermKeys {
    public static final Property<String> NAME = new PropertyImpl("name", String.class);
    public static final Property<Boolean> ACTIVE = new PropertyImpl("active", Boolean.class);
    public static final Property<Enum> TYPE = new PropertyImpl("type", Enum.class);
    public static final Property<String> IMPLEMENTATION_CLASS = new PropertyImpl("implementationClass", String.class);
}

