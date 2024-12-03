/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.application;

import java.util.Map;

public final class BooleanAttributeUtil {
    private BooleanAttributeUtil() {
    }

    static boolean getBooleanAttribute(Map<String, String> attributes, String name) {
        return Boolean.parseBoolean(attributes.get(name));
    }

    static void setBooleanAttribute(Map<String, String> attributes, String name, boolean value) {
        attributes.put(name, Boolean.toString(value));
    }
}

