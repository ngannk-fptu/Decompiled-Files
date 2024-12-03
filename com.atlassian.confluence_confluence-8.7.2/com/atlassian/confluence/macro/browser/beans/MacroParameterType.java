/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.macro.browser.beans;

import com.google.common.collect.ImmutableMap;
import java.util.EnumSet;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;

public enum MacroParameterType {
    STRING("string"),
    BOOLEAN("boolean"),
    USERNAME("username"),
    ENUM("enum"),
    INT("int"),
    SPACE_KEY("spacekey"),
    RELATIVE_DATE("relativedate"),
    PERCENTAGE("percentage"),
    CONFLUENCE_CONTENT("confluence-content"),
    URL("url"),
    COLOR("color"),
    ATTACHMENT("attachment"),
    FULL_ATTACHMENT("full_attachment"),
    LABEL("label"),
    DATE("date"),
    GROUP("group"),
    CQL("cql");

    private static final Map<String, MacroParameterType> lookupByTypeName;
    private final String typeName;

    private MacroParameterType(String typeName) {
        this.typeName = typeName;
    }

    public String getName() {
        return this.typeName;
    }

    public static @Nullable MacroParameterType get(String typeName) {
        return lookupByTypeName.get(typeName);
    }

    public String toString() {
        return this.typeName;
    }

    static {
        ImmutableMap.Builder lookupMapBuilder = ImmutableMap.builder();
        for (MacroParameterType type : EnumSet.allOf(MacroParameterType.class)) {
            lookupMapBuilder.put((Object)type.getName(), (Object)type);
        }
        lookupByTypeName = lookupMapBuilder.build();
    }
}

