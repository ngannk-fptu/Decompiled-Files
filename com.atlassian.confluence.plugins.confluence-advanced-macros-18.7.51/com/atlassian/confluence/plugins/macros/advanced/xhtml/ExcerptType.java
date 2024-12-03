/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.macros.advanced.xhtml;

public enum ExcerptType {
    NONE("none"),
    LEGACY("simple"),
    RENDERED("rich content");

    private final String value;

    private ExcerptType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        return this.getValue();
    }

    public static ExcerptType fromString(String value) {
        for (ExcerptType type : ExcerptType.values()) {
            if (!type.getValue().equalsIgnoreCase(value)) continue;
            return type;
        }
        return NONE;
    }

    public static ExcerptType fromOldValue(String oldValue) {
        return Boolean.valueOf(oldValue) != false ? LEGACY : NONE;
    }
}

