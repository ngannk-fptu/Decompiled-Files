/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Iterables
 */
package com.atlassian.streams.api.common;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Predicates {
    @Deprecated
    public static com.google.common.base.Predicate<String> containsAnyString(Iterable<String> xs) {
        return com.google.common.base.Predicates.or((Iterable)Iterables.transform(xs, Predicates.containsString()));
    }

    @Deprecated
    public static Function<String, com.google.common.base.Predicate<String>> containsString() {
        return ContainsStringFunction.INSTANCE;
    }

    @Deprecated
    public static com.google.common.base.Predicate<String> containsString(String x) {
        return (com.google.common.base.Predicate)Predicates.containsString().apply((Object)x);
    }

    @Deprecated
    public static <A> com.google.common.base.Predicate<A> contains(Iterable<A> xs) {
        return new Contains(xs);
    }

    @Deprecated
    public static com.google.common.base.Predicate<String> containsAnyIssueKey(Iterable<String> keys) {
        return com.google.common.base.Predicates.or((Iterable)Iterables.transform(keys, Predicates.containsIssueKey()));
    }

    @Deprecated
    public static com.google.common.base.Predicate<String> containsIssueKey(String key) {
        return (com.google.common.base.Predicate)Predicates.containsIssueKey().apply((Object)key);
    }

    public static Predicate<String> containsIssueKeyPredicate(String key) {
        return (Predicate)Predicates.containsIssueKey().apply((Object)key);
    }

    @Deprecated
    public static Function<String, com.google.common.base.Predicate<String>> containsIssueKey() {
        return ContainsIssueKeyFunction.INSTANCE;
    }

    private static final class ContainsIssueKey
    implements com.google.common.base.Predicate<String> {
        private final String key;
        private final Pattern pattern;

        public ContainsIssueKey(String key) {
            this.key = key;
            String regex = "(?<!CR-)\\b" + key + "\\b";
            this.pattern = Pattern.compile(regex);
        }

        public boolean apply(String input) {
            return this.pattern.matcher(input).find();
        }

        public String toString() {
            return String.format("containsIssueKey(%s)", this.key);
        }
    }

    @Deprecated
    private static enum ContainsIssueKeyFunction implements Function<String, com.google.common.base.Predicate<String>>
    {
        INSTANCE;


        public com.google.common.base.Predicate<String> apply(String key) {
            return new ContainsIssueKey(key);
        }
    }

    @Deprecated
    private static final class Contains<A>
    implements com.google.common.base.Predicate<A> {
        private final Iterable<A> xs;

        private Contains(Iterable<A> xs) {
            this.xs = xs;
        }

        public boolean apply(A x) {
            return Iterables.contains(this.xs, x);
        }

        public String toString() {
            return String.format("contains(%s)", this.xs);
        }
    }

    @Deprecated
    private static final class ContainsString
    implements com.google.common.base.Predicate<String> {
        private final String s;

        public ContainsString(String s) {
            this.s = s;
        }

        public boolean apply(String input) {
            return input.contains(this.s);
        }

        public String toString() {
            return String.format("containsString(%s)", this.s);
        }
    }

    @Deprecated
    private static enum ContainsStringFunction implements Function<String, com.google.common.base.Predicate<String>>
    {
        INSTANCE;


        public com.google.common.base.Predicate<String> apply(String s) {
            return new ContainsString(s);
        }
    }
}

