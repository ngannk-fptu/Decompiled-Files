/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.matcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.utility.nullability.MaybeNull;
import net.bytebuddy.utility.nullability.UnknownNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ElementMatcher<T> {
    public boolean matches(@UnknownNull T var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface Junction<S>
    extends ElementMatcher<S> {
        public <U extends S> Junction<U> and(ElementMatcher<? super U> var1);

        public <U extends S> Junction<U> or(ElementMatcher<? super U> var1);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static abstract class ForNonNullValues<W>
        extends AbstractBase<W> {
            @Override
            public boolean matches(@MaybeNull W target) {
                return target != null && this.doMatch(target);
            }

            protected abstract boolean doMatch(W var1);

            public boolean equals(@MaybeNull Object object) {
                if (this == object) {
                    return true;
                }
                if (object == null) {
                    return false;
                }
                return this.getClass() == object.getClass();
            }

            public int hashCode() {
                return this.getClass().hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Disjunction<W>
        extends AbstractBase<W> {
            private final List<ElementMatcher<? super W>> matchers;

            public Disjunction(ElementMatcher<? super W> ... matcher) {
                this(Arrays.asList(matcher));
            }

            public Disjunction(List<ElementMatcher<? super W>> matchers) {
                this.matchers = new ArrayList<ElementMatcher<? super W>>(matchers.size());
                for (ElementMatcher<W> elementMatcher : matchers) {
                    if (elementMatcher instanceof Disjunction) {
                        this.matchers.addAll(((Disjunction)elementMatcher).matchers);
                        continue;
                    }
                    this.matchers.add(elementMatcher);
                }
            }

            @Override
            public boolean matches(@UnknownNull W target) {
                for (ElementMatcher<W> matcher : this.matchers) {
                    if (!matcher.matches(target)) continue;
                    return true;
                }
                return false;
            }

            public String toString() {
                StringBuilder stringBuilder = new StringBuilder("(");
                boolean first = true;
                for (ElementMatcher<? super W> elementMatcher : this.matchers) {
                    if (first) {
                        first = false;
                    } else {
                        stringBuilder.append(" or ");
                    }
                    stringBuilder.append(elementMatcher);
                }
                return stringBuilder.append(")").toString();
            }

            public boolean equals(@MaybeNull Object object) {
                if (this == object) {
                    return true;
                }
                if (object == null) {
                    return false;
                }
                if (this.getClass() != object.getClass()) {
                    return false;
                }
                return ((Object)this.matchers).equals(((Disjunction)object).matchers);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + ((Object)this.matchers).hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Conjunction<W>
        extends AbstractBase<W> {
            private final List<ElementMatcher<? super W>> matchers;

            public Conjunction(ElementMatcher<? super W> ... matcher) {
                this(Arrays.asList(matcher));
            }

            public Conjunction(List<ElementMatcher<? super W>> matchers) {
                this.matchers = new ArrayList<ElementMatcher<? super W>>(matchers.size());
                for (ElementMatcher<W> elementMatcher : matchers) {
                    if (elementMatcher instanceof Conjunction) {
                        this.matchers.addAll(((Conjunction)elementMatcher).matchers);
                        continue;
                    }
                    this.matchers.add(elementMatcher);
                }
            }

            @Override
            public boolean matches(@UnknownNull W target) {
                for (ElementMatcher<W> matcher : this.matchers) {
                    if (matcher.matches(target)) continue;
                    return false;
                }
                return true;
            }

            public String toString() {
                StringBuilder stringBuilder = new StringBuilder("(");
                boolean first = true;
                for (ElementMatcher<? super W> elementMatcher : this.matchers) {
                    if (first) {
                        first = false;
                    } else {
                        stringBuilder.append(" and ");
                    }
                    stringBuilder.append(elementMatcher);
                }
                return stringBuilder.append(")").toString();
            }

            public boolean equals(@MaybeNull Object object) {
                if (this == object) {
                    return true;
                }
                if (object == null) {
                    return false;
                }
                if (this.getClass() != object.getClass()) {
                    return false;
                }
                return ((Object)this.matchers).equals(((Conjunction)object).matchers);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + ((Object)this.matchers).hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static abstract class AbstractBase<V>
        implements Junction<V> {
            @Override
            public <U extends V> Junction<U> and(ElementMatcher<? super U> other) {
                return new Conjunction(this, other);
            }

            @Override
            public <U extends V> Junction<U> or(ElementMatcher<? super U> other) {
                return new Disjunction(this, other);
            }
        }
    }
}

