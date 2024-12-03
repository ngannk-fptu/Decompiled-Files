/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.confluence.api.model.content;

import com.atlassian.annotations.ExperimentalApi;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

@ExperimentalApi
public final class Position
implements Comparable<Position> {
    public static final String POSITION_KEY = "position";
    public static final Position NONE = new Position(null);
    private static final String NONE_STRING = "none";
    private final Integer value;

    private Position(Integer value) {
        this.value = value;
    }

    public static Position of(Integer position) {
        if (position == null) {
            return NONE;
        }
        return new Position(position);
    }

    @JsonValue
    public Object serialise() {
        if (this == NONE) {
            return NONE_STRING;
        }
        return this.value;
    }

    @JsonCreator
    public static Position deserialise(String str) {
        if (NONE_STRING.equals(str)) {
            return NONE;
        }
        return Position.of(Integer.parseInt(str));
    }

    @Override
    public int compareTo(Position p) {
        if (p == null) {
            throw new IllegalArgumentException("Cannot compare Position with null");
        }
        if (this.equals(p)) {
            return 0;
        }
        if (this == NONE) {
            return 1;
        }
        if (p == NONE) {
            return -1;
        }
        return this.value.compareTo(p.value);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Position position = (Position)o;
        return !(this.value != null ? !this.value.equals(position.value) : position.value != null);
    }

    public int hashCode() {
        return this.value != null ? this.value : -1;
    }
}

