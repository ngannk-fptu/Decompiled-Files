/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.nodes;

import com.hazelcast.org.snakeyaml.engine.v2.common.UriEncoder;
import java.util.Objects;

public final class Tag {
    public static final String PREFIX = "tag:yaml.org,2002:";
    public static final Tag SET = new Tag("tag:yaml.org,2002:set");
    public static final Tag BINARY = new Tag("tag:yaml.org,2002:binary");
    public static final Tag INT = new Tag("tag:yaml.org,2002:int");
    public static final Tag FLOAT = new Tag("tag:yaml.org,2002:float");
    public static final Tag BOOL = new Tag("tag:yaml.org,2002:bool");
    public static final Tag NULL = new Tag("tag:yaml.org,2002:null");
    public static final Tag STR = new Tag("tag:yaml.org,2002:str");
    public static final Tag SEQ = new Tag("tag:yaml.org,2002:seq");
    public static final Tag MAP = new Tag("tag:yaml.org,2002:map");
    public static final Tag ENV_TAG = new Tag("!ENV_VARIABLE");
    private final String value;

    public Tag(String tag) {
        Objects.requireNonNull(tag, "Tag must be provided.");
        if (tag.isEmpty()) {
            throw new IllegalArgumentException("Tag must not be empty.");
        }
        if (tag.trim().length() != tag.length()) {
            throw new IllegalArgumentException("Tag must not contain leading or trailing spaces.");
        }
        this.value = UriEncoder.encode(tag);
    }

    public Tag(Class<? extends Object> clazz) {
        Objects.requireNonNull(clazz, "Class for tag must be provided.");
        this.value = PREFIX + UriEncoder.encode(clazz.getName());
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        return this.value;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Tag) {
            return this.value.equals(((Tag)obj).getValue());
        }
        return false;
    }

    public int hashCode() {
        return this.value.hashCode();
    }
}

