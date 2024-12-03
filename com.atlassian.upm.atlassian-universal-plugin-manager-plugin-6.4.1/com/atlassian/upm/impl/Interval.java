/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.ImmutableBiMap
 *  javax.annotation.Nullable
 */
package com.atlassian.upm.impl;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.Objects;
import javax.annotation.Nullable;

public class Interval<T extends Comparable<T>> {
    @Nullable
    private final Floor<T> floor;
    @Nullable
    private final Ceiling<T> ceiling;

    public Interval(@Nullable Floor<T> floor, @Nullable Ceiling<T> ceiling) {
        this.floor = floor;
        this.ceiling = ceiling;
    }

    @Nullable
    public Floor<T> getFloor() {
        return this.floor;
    }

    @Nullable
    public Ceiling<T> getCeiling() {
        return this.ceiling;
    }

    public boolean contains(T value) {
        return !(this.floor != null && !this.floor.contains(value) || this.ceiling != null && !this.ceiling.contains(value));
    }

    public String toString() {
        return String.format("%s,%s", this.floor == null ? "(_" : this.floor, this.ceiling == null ? "_)" : this.ceiling);
    }

    public static final class Ceiling<T extends Comparable<T>>
    extends Bound<T> {
        private static final BiMap<Bound.Type, Character> typeToChar = ImmutableBiMap.of((Object)((Object)Bound.Type.INCLUSIVE), (Object)Character.valueOf(']'), (Object)((Object)Bound.Type.EXCLUSIVE), (Object)Character.valueOf(')'));
        private static final BiMap<Character, Bound.Type> charToType = typeToChar.inverse();

        public Ceiling(T value, Bound.Type type) {
            super(value, type);
        }

        @Override
        public boolean contains(T value) {
            int comparison = this.getValue().compareTo(value);
            switch (this.getType()) {
                case INCLUSIVE: {
                    if (comparison >= 0) break;
                    return false;
                }
                case EXCLUSIVE: {
                    if (comparison > 0) break;
                    return false;
                }
            }
            return true;
        }

        public static Bound.Type getType(char type) {
            return (Bound.Type)((Object)charToType.get((Object)Character.valueOf(type)));
        }

        public String toString() {
            return String.format("%s%c", this.getValue(), typeToChar.get((Object)this.getType()));
        }
    }

    public static final class Floor<T extends Comparable<T>>
    extends Bound<T> {
        private static final BiMap<Bound.Type, Character> typeToChar = ImmutableBiMap.of((Object)((Object)Bound.Type.INCLUSIVE), (Object)Character.valueOf('['), (Object)((Object)Bound.Type.EXCLUSIVE), (Object)Character.valueOf('('));
        private static final BiMap<Character, Bound.Type> charToType = typeToChar.inverse();

        public Floor(T value, Bound.Type type) {
            super(value, type);
        }

        @Override
        public boolean contains(T value) {
            int comparison = this.getValue().compareTo(value);
            switch (this.getType()) {
                case INCLUSIVE: {
                    if (comparison <= 0) break;
                    return false;
                }
                case EXCLUSIVE: {
                    if (comparison < 0) break;
                    return false;
                }
            }
            return true;
        }

        public static Bound.Type getType(char type) {
            return (Bound.Type)((Object)charToType.get((Object)Character.valueOf(type)));
        }

        public String toString() {
            return String.format("%c%s", typeToChar.get((Object)this.getType()), this.getValue());
        }
    }

    public static abstract class Bound<T extends Comparable<T>> {
        private final T value;
        private final Type type;

        public Bound(T value, Type type) {
            this.value = (Comparable)Objects.requireNonNull(value, "value");
            this.type = Objects.requireNonNull(type, "type");
        }

        public T getValue() {
            return this.value;
        }

        public Type getType() {
            return this.type;
        }

        public abstract boolean contains(T var1);

        public static enum Type {
            INCLUSIVE,
            EXCLUSIVE;

        }
    }
}

