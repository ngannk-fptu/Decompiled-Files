/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.util;

public abstract class FuzzyBoolean {
    public static final FuzzyBoolean YES = new YesFuzzyBoolean();
    public static final FuzzyBoolean NO = new NoFuzzyBoolean();
    public static final FuzzyBoolean MAYBE = new MaybeFuzzyBoolean();
    public static final FuzzyBoolean NEVER = new NeverFuzzyBoolean();

    public abstract boolean alwaysTrue();

    public abstract boolean alwaysFalse();

    public abstract boolean maybeTrue();

    public abstract boolean maybeFalse();

    public abstract FuzzyBoolean and(FuzzyBoolean var1);

    public abstract FuzzyBoolean or(FuzzyBoolean var1);

    public abstract FuzzyBoolean not();

    public static final FuzzyBoolean fromBoolean(boolean b) {
        return b ? YES : NO;
    }

    private static class MaybeFuzzyBoolean
    extends FuzzyBoolean {
        private MaybeFuzzyBoolean() {
        }

        @Override
        public boolean alwaysFalse() {
            return false;
        }

        @Override
        public boolean alwaysTrue() {
            return false;
        }

        @Override
        public boolean maybeFalse() {
            return true;
        }

        @Override
        public boolean maybeTrue() {
            return true;
        }

        @Override
        public FuzzyBoolean and(FuzzyBoolean other) {
            return other.alwaysFalse() ? other : this;
        }

        @Override
        public FuzzyBoolean not() {
            return this;
        }

        @Override
        public FuzzyBoolean or(FuzzyBoolean other) {
            return other.alwaysTrue() ? other : this;
        }

        public String toString() {
            return "MAYBE";
        }
    }

    private static class NeverFuzzyBoolean
    extends FuzzyBoolean {
        private NeverFuzzyBoolean() {
        }

        @Override
        public boolean alwaysFalse() {
            return true;
        }

        @Override
        public boolean alwaysTrue() {
            return false;
        }

        @Override
        public boolean maybeFalse() {
            return true;
        }

        @Override
        public boolean maybeTrue() {
            return false;
        }

        @Override
        public FuzzyBoolean and(FuzzyBoolean other) {
            return this;
        }

        @Override
        public FuzzyBoolean not() {
            return this;
        }

        @Override
        public FuzzyBoolean or(FuzzyBoolean other) {
            return this;
        }

        public String toString() {
            return "NEVER";
        }
    }

    private static class NoFuzzyBoolean
    extends FuzzyBoolean {
        private NoFuzzyBoolean() {
        }

        @Override
        public boolean alwaysFalse() {
            return true;
        }

        @Override
        public boolean alwaysTrue() {
            return false;
        }

        @Override
        public boolean maybeFalse() {
            return true;
        }

        @Override
        public boolean maybeTrue() {
            return false;
        }

        @Override
        public FuzzyBoolean and(FuzzyBoolean other) {
            return this;
        }

        @Override
        public FuzzyBoolean not() {
            return YES;
        }

        @Override
        public FuzzyBoolean or(FuzzyBoolean other) {
            return other;
        }

        public String toString() {
            return "NO";
        }
    }

    private static class YesFuzzyBoolean
    extends FuzzyBoolean {
        private YesFuzzyBoolean() {
        }

        @Override
        public boolean alwaysFalse() {
            return false;
        }

        @Override
        public boolean alwaysTrue() {
            return true;
        }

        @Override
        public boolean maybeFalse() {
            return false;
        }

        @Override
        public boolean maybeTrue() {
            return true;
        }

        @Override
        public FuzzyBoolean and(FuzzyBoolean other) {
            return other;
        }

        @Override
        public FuzzyBoolean not() {
            return NO;
        }

        @Override
        public FuzzyBoolean or(FuzzyBoolean other) {
            return this;
        }

        public String toString() {
            return "YES";
        }
    }
}

