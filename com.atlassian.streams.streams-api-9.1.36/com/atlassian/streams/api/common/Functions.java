/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.streams.api.common;

import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.api.common.Option;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import java.net.URI;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public final class Functions {
    private Functions() {
    }

    @Deprecated
    public static <T> Function<Supplier<? extends T>, T> fromSupplier() {
        return new ValueExtractor();
    }

    @Deprecated
    public static <T> Function<Supplier<T>, Supplier<T>> ignoreExceptions() {
        return new ExceptionIgnorer();
    }

    @Deprecated
    public static <T> Function<T, List<T>> singletonList(Class<T> c) {
        return new SingletonList();
    }

    @Deprecated
    public static Function<String, Long> parseLong() {
        return ParseLong.INSTANCE;
    }

    @Deprecated
    public static Function<String, Either<NumberFormatException, Integer>> parseInt() {
        return ParseInt.INSTANCE;
    }

    @Deprecated
    public static Function<Integer, Integer> max(int i) {
        return new MaxInt(i);
    }

    @Deprecated
    public static Function<String, Option<String>> trimToNone() {
        return TrimToNone.INSTANCE;
    }

    public Option<String> trimToNone(String s) {
        return Option.option(StringUtils.trimToNull((String)s));
    }

    @Deprecated
    public static Function<String, URI> toUri() {
        return ToUri.INSTANCE;
    }

    URI toUri(String from) {
        return URI.create(from);
    }

    @Deprecated
    public static Function<URI, String> uriToASCIIString() {
        return UriToASCIIString.INSTANCE;
    }

    @Deprecated
    public static <A, B> Function<Function<A, B>, B> apply(final A a) {
        return new Function<Function<A, B>, B>(){

            public B apply(Function<A, B> f) {
                return f.apply(a);
            }
        };
    }

    @Deprecated
    private static enum UriToASCIIString implements Function<URI, String>
    {
        INSTANCE;


        public String apply(URI from) {
            return from.toASCIIString();
        }
    }

    @Deprecated
    private static enum ToUri implements Function<String, URI>
    {
        INSTANCE;


        public URI apply(String from) {
            return URI.create(from);
        }
    }

    @Deprecated
    private static enum TrimToNone implements Function<String, Option<String>>
    {
        INSTANCE;


        public Option<String> apply(String s) {
            return Option.option(StringUtils.trimToNull((String)s));
        }
    }

    @Deprecated
    private static final class MaxInt
    implements Function<Integer, Integer> {
        private final int i;

        public MaxInt(int i) {
            this.i = i;
        }

        public Integer apply(Integer i2) {
            return Math.max(this.i, i2);
        }
    }

    @Deprecated
    private static enum ParseInt implements Function<String, Either<NumberFormatException, Integer>>
    {
        INSTANCE;


        public Either<NumberFormatException, Integer> apply(String s) {
            try {
                return Either.right(Integer.valueOf(s));
            }
            catch (NumberFormatException e) {
                return Either.left(e);
            }
        }
    }

    @Deprecated
    private static enum ParseLong implements Function<String, Long>
    {
        INSTANCE;


        public Long apply(String s) {
            return Long.valueOf(s);
        }
    }

    @Deprecated
    private static final class SingletonList<T>
    implements Function<T, List<T>> {
        private SingletonList() {
        }

        public List<T> apply(T o) {
            return ImmutableList.of(o);
        }
    }

    @Deprecated
    static class IgnoreAndReturnNull<T>
    implements Supplier<T> {
        private final Supplier<T> delegate;

        IgnoreAndReturnNull(Supplier<T> delegate) {
            this.delegate = (Supplier)Preconditions.checkNotNull(delegate);
        }

        public T get() {
            try {
                return (T)this.delegate.get();
            }
            catch (RuntimeException ignore) {
                return null;
            }
        }
    }

    @Deprecated
    static class ExceptionIgnorer<T>
    implements Function<Supplier<T>, Supplier<T>> {
        ExceptionIgnorer() {
        }

        public Supplier<T> apply(Supplier<T> from) {
            return new IgnoreAndReturnNull<T>(from);
        }
    }

    private static class ValueExtractor<T>
    implements Function<Supplier<? extends T>, T> {
        private ValueExtractor() {
        }

        public T apply(Supplier<? extends T> supplier) {
            return (T)supplier.get();
        }
    }
}

