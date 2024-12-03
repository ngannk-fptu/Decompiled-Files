/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Index {
    public static final Index CONTENT = new Index("content", Type.SYSTEM);
    public static final Index CHANGE = new Index("change", Type.SYSTEM);
    private final String name;
    private final Type type;

    private Index(String name, Type type) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
    }

    public String getName() {
        return this.name;
    }

    public Type getType() {
        return this.type;
    }

    public static Index custom(String name) {
        return new Index(name, Type.CUSTOM);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Index that = (Index)o;
        return Objects.equals(this.name, that.name) && this.type == that.type;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.name, this.type});
    }

    public String toString() {
        return "Index{name='" + this.name + "', type=" + this.type + "}";
    }

    public static List<Index> from(EnumSet<SearchIndex> indexes) {
        return indexes.stream().map(index -> {
            switch (index) {
                case CONTENT: {
                    return CONTENT;
                }
                case CHANGE: {
                    return CHANGE;
                }
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static enum Type {
        SYSTEM,
        CUSTOM;

    }
}

