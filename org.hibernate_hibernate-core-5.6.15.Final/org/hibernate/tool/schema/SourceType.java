/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema;

public enum SourceType {
    METADATA("metadata"),
    SCRIPT("script"),
    METADATA_THEN_SCRIPT("metadata-then-script"),
    SCRIPT_THEN_METADATA("script-then-metadata");

    private final String externalName;

    private SourceType(String externalName) {
        this.externalName = externalName;
    }

    public static SourceType interpret(Object value, SourceType defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (SourceType.class.isInstance(value)) {
            return (SourceType)((Object)value);
        }
        String name = value.toString().trim();
        if (name.isEmpty()) {
            return defaultValue;
        }
        if (SourceType.METADATA.externalName.equals(value)) {
            return METADATA;
        }
        if (SourceType.SCRIPT.externalName.equals(value)) {
            return SCRIPT;
        }
        if (SourceType.METADATA_THEN_SCRIPT.externalName.equals(value)) {
            return METADATA_THEN_SCRIPT;
        }
        if (SourceType.SCRIPT_THEN_METADATA.externalName.equals(value)) {
            return SCRIPT_THEN_METADATA;
        }
        throw new IllegalArgumentException("Unrecognized schema generation source-type value : '" + value + '\'');
    }
}

