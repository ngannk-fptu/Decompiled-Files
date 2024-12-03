/*
 * Decompiled with CFR 0.152.
 */
package brave.sampler;

import brave.sampler.Matcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public final class Matchers {
    public static <P> Matcher<P> alwaysMatch() {
        return Constants.ALWAYS_MATCH;
    }

    public static <P> Matcher<P> neverMatch() {
        return Constants.NEVER_MATCH;
    }

    public static <P> Matcher<P> and(Iterable<? extends Matcher<P>> matchers) {
        return Matchers.and(Matchers.toArray(matchers));
    }

    public static <P> Matcher<P> and(Matcher<P> ... matchers) {
        return Matchers.composite(matchers, true);
    }

    public static <P> Matcher<P> or(Iterable<? extends Matcher<P>> matchers) {
        return Matchers.or(Matchers.toArray(matchers));
    }

    public static <P> Matcher<P> or(Matcher<P> ... matchers) {
        return Matchers.composite(matchers, false);
    }

    static <P> Matcher[] toArray(Iterable<? extends Matcher<P>> matchers) {
        if (matchers == null) {
            throw new NullPointerException("matchers == null");
        }
        if (matchers instanceof Collection) {
            return ((Collection)matchers).toArray(new Matcher[0]);
        }
        ArrayList<Matcher<P>> result = new ArrayList<Matcher<P>>();
        for (Matcher<P> matcher : matchers) {
            result.add(matcher);
        }
        return result.toArray(new Matcher[0]);
    }

    static <P> Matcher<P> composite(Matcher<P>[] matchers, boolean and) {
        if (matchers == null) {
            throw new NullPointerException("matchers == null");
        }
        if (matchers.length == 0) {
            return Matchers.neverMatch();
        }
        for (int i = 0; i < matchers.length; ++i) {
            if (matchers[i] != null) continue;
            throw new NullPointerException("matchers[" + i + "] == null");
        }
        if (matchers.length == 1) {
            return matchers[0];
        }
        return and ? new And<P>(matchers) : new Or<P>(matchers);
    }

    static class Or<P>
    implements Matcher<P> {
        final Matcher<P>[] matchers;

        Or(Matcher<P>[] matchers) {
            this.matchers = Arrays.copyOf(matchers, matchers.length);
        }

        @Override
        public boolean matches(P parameters) {
            for (Matcher<P> matcher : this.matchers) {
                if (!matcher.matches(parameters)) continue;
                return true;
            }
            return false;
        }

        public String toString() {
            return "Or(" + Arrays.toString(this.matchers) + ")";
        }
    }

    static class And<P>
    implements Matcher<P> {
        final Matcher<P>[] matchers;

        And(Matcher<P>[] matchers) {
            this.matchers = Arrays.copyOf(matchers, matchers.length);
        }

        @Override
        public boolean matches(P parameters) {
            for (Matcher<P> matcher : this.matchers) {
                if (matcher.matches(parameters)) continue;
                return false;
            }
            return true;
        }

        public String toString() {
            return "And(" + Arrays.toString(this.matchers) + ")";
        }
    }

    static enum Constants implements Matcher<Object>
    {
        ALWAYS_MATCH{

            @Override
            public boolean matches(Object parameters) {
                return true;
            }

            public String toString() {
                return "matchAll()";
            }
        }
        ,
        NEVER_MATCH{

            @Override
            public boolean matches(Object parameters) {
                return false;
            }

            public String toString() {
                return "neverMatch()";
            }
        };

    }
}

