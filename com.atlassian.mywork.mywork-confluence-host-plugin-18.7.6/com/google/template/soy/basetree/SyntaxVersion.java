/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package com.google.template.soy.basetree;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public enum SyntaxVersion {
    V1_0,
    V2_0,
    V2_1,
    V2_2,
    V2_3,
    V9_9;

    private static final SyntaxVersion MAX_PUBLIC_SYNTAX_VERSION;
    private static final Map<String, SyntaxVersion> NAME_TO_INSTANCE_MAP;
    public final String name;
    public final int num;

    public static SyntaxVersion forName(String name) {
        SyntaxVersion version = NAME_TO_INSTANCE_MAP.get(name);
        if (version == null) {
            throw new RuntimeException("Invalid Soy syntax version \"" + name + "\".");
        }
        if (version.num > SyntaxVersion.MAX_PUBLIC_SYNTAX_VERSION.num) {
            throw new RuntimeException("It appears you are one of the first users attempting to manually declare Soy syntax version " + name + ". It's not currently enabled for declaration, but the Soy team can probably enable it if you drop them a note.");
        }
        return version;
    }

    private SyntaxVersion() {
        String[] parts = this.name().substring(1).split("_", 2);
        this.name = parts[0] + "." + parts[1];
        this.num = Integer.parseInt(parts[0]) * 1000 + Integer.parseInt(parts[1]);
    }

    public String toString() {
        return this.name;
    }

    static {
        MAX_PUBLIC_SYNTAX_VERSION = V2_0;
        ImmutableMap.Builder nameToInstanceMapBuilder = ImmutableMap.builder();
        for (SyntaxVersion version : SyntaxVersion.values()) {
            nameToInstanceMapBuilder.put((Object)version.name, (Object)version);
        }
        NAME_TO_INSTANCE_MAP = nameToInstanceMapBuilder.build();
    }
}

