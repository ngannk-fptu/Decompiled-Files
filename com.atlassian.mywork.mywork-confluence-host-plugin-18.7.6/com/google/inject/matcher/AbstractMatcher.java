/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.matcher;

import com.google.inject.matcher.Matcher;
import java.io.Serializable;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractMatcher<T>
implements Matcher<T> {
    @Override
    public Matcher<T> and(Matcher<? super T> other) {
        return new AndMatcher<T>(this, other);
    }

    @Override
    public Matcher<T> or(Matcher<? super T> other) {
        return new OrMatcher<T>(this, other);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class OrMatcher<T>
    extends AbstractMatcher<T>
    implements Serializable {
        private final Matcher<? super T> a;
        private final Matcher<? super T> b;
        private static final long serialVersionUID = 0L;

        public OrMatcher(Matcher<? super T> a, Matcher<? super T> b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean matches(T t) {
            return this.a.matches(t) || this.b.matches(t);
        }

        public boolean equals(Object other) {
            return other instanceof OrMatcher && ((OrMatcher)other).a.equals(this.a) && ((OrMatcher)other).b.equals(this.b);
        }

        public int hashCode() {
            return 37 * (this.a.hashCode() ^ this.b.hashCode());
        }

        public String toString() {
            return "or(" + this.a + ", " + this.b + ")";
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class AndMatcher<T>
    extends AbstractMatcher<T>
    implements Serializable {
        private final Matcher<? super T> a;
        private final Matcher<? super T> b;
        private static final long serialVersionUID = 0L;

        public AndMatcher(Matcher<? super T> a, Matcher<? super T> b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean matches(T t) {
            return this.a.matches(t) && this.b.matches(t);
        }

        public boolean equals(Object other) {
            return other instanceof AndMatcher && ((AndMatcher)other).a.equals(this.a) && ((AndMatcher)other).b.equals(this.b);
        }

        public int hashCode() {
            return 41 * (this.a.hashCode() ^ this.b.hashCode());
        }

        public String toString() {
            return "and(" + this.a + ", " + this.b + ")";
        }
    }
}

