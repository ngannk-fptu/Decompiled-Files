/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.search.query.entity.restriction;

import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.PropertyImpl;

public class PropertyUtils {
    public static Property<String> ofTypeString(String name) {
        return new PropertyImpl<String>(name, String.class);
    }

    public static Property<Enum> ofTypeEnum(String name) {
        return new PropertyImpl<Enum>(name, Enum.class);
    }

    public static Property<Boolean> ofTypeBoolean(String name) {
        return new PropertyImpl<Boolean>(name, Boolean.class);
    }
}

