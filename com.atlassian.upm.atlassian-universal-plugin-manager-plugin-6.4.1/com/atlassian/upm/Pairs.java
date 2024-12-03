/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.upm;

import javax.annotation.Nullable;

public interface Pairs {

    public static final class MutablePair<T1, T2>
    implements Pair<T1, T2> {
        @Nullable
        private T1 first;
        @Nullable
        private T2 second;

        private MutablePair(@Nullable T1 first, @Nullable T2 second) {
            this.first = first;
            this.second = second;
        }

        public static <T1, T2> MutablePair<T1, T2> pair(@Nullable T1 first, @Nullable T2 second) {
            return new MutablePair<T1, T2>(first, second);
        }

        @Override
        @Nullable
        public T1 getFirst() {
            return this.first;
        }

        public void setFirst(@Nullable T1 first) {
            this.first = first;
        }

        @Override
        @Nullable
        public T2 getSecond() {
            return this.second;
        }

        public void setSecond(@Nullable T2 second) {
            this.second = second;
        }
    }

    public static final class ImmutablePair<T1, T2>
    implements Pair<T1, T2> {
        @Nullable
        private final T1 first;
        @Nullable
        private final T2 second;

        private ImmutablePair(@Nullable T1 first, @Nullable T2 second) {
            this.first = first;
            this.second = second;
        }

        public static <T1, T2> ImmutablePair<T1, T2> pair(@Nullable T1 first, @Nullable T2 second) {
            return new ImmutablePair<T1, T2>(first, second);
        }

        @Override
        @Nullable
        public T1 getFirst() {
            return this.first;
        }

        @Override
        @Nullable
        public T2 getSecond() {
            return this.second;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || this.getClass() != other.getClass()) {
                return false;
            }
            Pair otherPair = (Pair)other;
            return (this.first == null ? otherPair.getFirst() == null : this.first.equals(otherPair.getFirst())) && (this.second == null ? otherPair.getSecond() == null : this.second.equals(otherPair.getSecond()));
        }

        public int hashCode() {
            return (this.first == null ? 0 : this.first.hashCode()) + (this.second == null ? 0 : 31 * this.second.hashCode());
        }
    }

    public static interface Pair<T1, T2> {
        @Nullable
        public T1 getFirst();

        @Nullable
        public T2 getSecond();
    }
}

